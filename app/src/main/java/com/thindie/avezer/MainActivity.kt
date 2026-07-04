package com.thindie.avezer

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.thindie.avezer.application.Application
import com.thindie.avezer.engine.Route
import com.thindie.avezer.engine.Router
import com.thindie.avezer.engine.Section
import com.thindie.avezer.feature.home.HomeFlow
import com.thindie.avezer.feature.search.SearchFlow
import com.thindie.avezer.feature.settings.SettingsFlow
import com.thindie.avezer.feature.settings.domain.SettingsRepository
import com.thindie.avezer.uikit.AppTheme
import com.thindie.avezer.uikit.LocalThemeSwitcher
import com.thindie.avezer.uikit.ThemeSwitcher
import com.thindie.avezer.uikit.VSpacer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  private lateinit var app: Application
  private lateinit var router: Router

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    app = application as Application
    router = app.requireRouter()
    awaitFinish()
    setContent {
      SideEffect {
        HomeFlow(
          router = router,
          flowModule = app.applicationScope.appFlowModule,
        )
          .start()
      }
      val settingsRepository: SettingsRepository? = remember { app.applicationScope.settingsRepository }
      val themeSwitcher =
        remember(settingsRepository) {
          val switcher = ThemeSwitcher()
          if (settingsRepository != null) {
            val saved = settingsRepository.themeChoice()
            if (saved != null) {
              switcher.set(
                when (saved) {
                  is SettingsRepository.ThemeChoice.Auto -> ThemeSwitcher.Choice.Auto
                  is SettingsRepository.ThemeChoice.Light -> ThemeSwitcher.Choice.Light
                  is SettingsRepository.ThemeChoice.Dark -> ThemeSwitcher.Choice.Dark
                },
              )
            }
          }
          switcher
        }
      CompositionLocalProvider(
        LocalThemeSwitcher provides themeSwitcher,
      ) {
        val themeColors = LocalThemeSwitcher.current.themeFlow.collectAsState(null)
        val isDark =
          when (themeColors.value) {
            null -> isSystemInDarkTheme()
            ThemeSwitcher.Choice.Dark -> true
            ThemeSwitcher.Choice.Light -> false
            ThemeSwitcher.Choice.Auto -> isSystemInDarkTheme()
          }
        val view = LocalView.current
        if (!view.isInEditMode) {
          SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDark
          }
        }
        AppTheme(isDark) {
          // Back handler disabled intentionally — native back button
          // triggers router.pop(), and when stack is empty, onPopLast()
          // emits finishCommand which closes the activity.
          BackHandler { }
          val routes by router.route.collectAsState(null)
          var prev by remember { mutableStateOf<Pair<Route, Route?>?>(null) }
          val isPop = routes != null && prev != null && routes!!.first == prev!!.second
          LaunchedEffect(routes) { prev = routes }
          if (routes != null) {
            val tween = tween<IntOffset>(durationMillis = 280)
            AnimatedContent(
              modifier = Modifier.background(AppTheme.colors.backgroundPrimary),
              targetState = routes!!.first,
              transitionSpec = {
                if (routes?.first?.section is HomeSection && routes?.second == null && !isPop) {
                  ContentTransform(
                    targetContentEnter = EnterTransition.None,
                    initialContentExit = ExitTransition.None,
                    targetContentZIndex = 0f,
                    sizeTransform = null,
                  )
                } else if (isPop) {
                  slideInHorizontally(tween) { -it } + fadeIn(tween()) togetherWith
                    slideOutHorizontally(tween) { it } + fadeOut(tween())
                } else {
                  slideInHorizontally(tween) { it } + fadeIn(tween()) togetherWith
                    slideOutHorizontally(tween) { -it } + fadeOut(tween())
                }
              },
              label = "route",
            ) { route ->
              when (route.section) {
                Section.Leaf -> route.content.invoke()
                is HomeSection -> {
                  when (route.section as HomeSection) {
                    is HomeSection.Places -> {
                      Box(modifier = Modifier.systemBarsPadding()) {
                        route.content.invoke()
                        BottomNavigationBar(
                          modifier = Modifier.align(Alignment.BottomCenter),
                          onPlacesClick = {},
                          onSearchClick = { switchToSearch() },
                          onSettingsClick = { switchToSettings() },
                          selected = route.section,
                        )
                      }
                    }

                    is HomeSection.Search -> {
                      Box(modifier = Modifier.systemBarsPadding()) {
                        route.content.invoke()
                        BottomNavigationBar(
                          modifier = Modifier.align(Alignment.BottomCenter),
                          onPlacesClick = { switchToHome() },
                          onSearchClick = {},
                          onSettingsClick = { switchToSettings() },
                          selected = route.section,
                        )
                      }
                    }

                    is HomeSection.Settings -> {
                      Box(modifier = Modifier.systemBarsPadding()) {
                        route.content.invoke()
                        BottomNavigationBar(
                          modifier = Modifier.align(Alignment.BottomCenter),
                          onPlacesClick = { switchToHome() },
                          onSearchClick = { switchToSearch() },
                          onSettingsClick = {},
                          selected = route.section,
                        )
                      }
                    }
                  }
                }

                else -> error("Unexpected section")
              }
            }
          }
        }
      }
    }
  }

  private fun awaitFinish() {
    lifecycleScope.launch {
      (application as Application).finishCommand.first()
      finish()
    }
  }

  private fun switchToHome() {
    val flow = HomeFlow(router = router, flowModule = app.applicationScope.appFlowModule)
    flow.onFinishBuilder { result ->
      when (result) {
        HomeFlow.Result.Search -> switchToSearch()
        HomeFlow.Result.Settings -> switchToSettings()
      }
    }
    flow.switch()
  }

  private fun switchToSearch() {
    val flow = SearchFlow(router = router, flowModule = app.applicationScope.appFlowModule)
    flow.onFinishBuilder { result ->
      when (result) {
        is SearchFlow.Result.PlaceRequested -> {
          // TODO: handle selected place — e.g. navigate to Places with filter
        }
      }
    }
    flow.switch()
  }

  private fun switchToSettings() {
    SettingsFlow(
      router = router,
      repository = app.applicationScope.settingsRepository,
      context = app,
    ).switch()
  }
}

@Composable
fun BottomNavigationBar(
  modifier: Modifier = Modifier,
  onPlacesClick: () -> Unit,
  onSearchClick: () -> Unit,
  onSettingsClick: () -> Unit,
  selected: Section,
) {
  val sections =
    remember {
      listOf(
        HomeSection.Places,
        HomeSection.Search,
        HomeSection.Settings,
      )
    }

  Row(
    modifier =
      modifier
        .fillMaxWidth()
        .background(AppTheme.colors.backgroundPrimary),
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    sections.forEach {
      val (icon, title) =
        when (it) {
          HomeSection.Places -> R.drawable.ic_home_24 to R.string.places
          HomeSection.Search -> R.drawable.ic_search_24 to R.string.search_title
          HomeSection.Settings -> R.drawable.ic_settings_24 to R.string.settings_title
        }

      Section(
        title = stringResource(title),
        icon = painterResource(icon),
        onClick = {
          when (it) {
            HomeSection.Places -> onPlacesClick.invoke()
            HomeSection.Search -> onSearchClick.invoke()
            HomeSection.Settings -> onSettingsClick.invoke()
          }
        },
        isSelected = selected == it,
      )
    }
  }
}

@Composable
fun Section(
  modifier: Modifier = Modifier,
  title: String,
  icon: Painter,
  onClick: () -> Unit,
  isSelected: Boolean,
) {
  val color by animateColorAsState(
    targetValue =
      when {
        isSelected -> {
          AppTheme.colors.accentPrimary
        }

        else -> {
          AppTheme.colors.contentSecondary
        }
      },
    animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing),
  )
  Column(
    modifier =
      modifier
        .clickable(
          onClick = onClick,
          indication = null,
          interactionSource = null,
        ),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    VSpacer(16.dp)
    Icon(
      painter = icon,
      contentDescription = null,
      tint = color,
      modifier = Modifier.size(40.dp),
    )
    Text(
      text = title,
      style = AppTheme.typography.labelMedium,
      color = color,
      modifier = Modifier.padding(top = 4.dp),
      maxLines = 1,
    )
    VSpacer(8.dp)
  }
}

@Immutable
sealed interface HomeSection : Section {
  @Immutable
  data object Search : HomeSection

  @Immutable
  data object Settings : HomeSection

  @Immutable
  data object Places : HomeSection
}
