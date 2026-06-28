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
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.thindie.avezer.application.Application
import com.thindie.avezer.engine.Route
import com.thindie.avezer.engine.Section
import com.thindie.avezer.feature.home.HomeFlow
import com.thindie.avezer.feature.settings.SettingsFlow
import com.thindie.avezer.uikit.AppTheme
import com.thindie.avezer.uikit.LocalThemeSwitcher
import com.thindie.avezer.uikit.ThemeSwitcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    val app = application as Application
    val router = app.requireRouter()
    awaitFinish()
    setContent {
      SideEffect {
        HomeFlow(
          router = router,
          flowModule = app.applicationScope.appFlowModule,
        )
          .start()
      }
      val themeSwitcher = remember { ThemeSwitcher() }
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
                          onSettingsClick = { SettingsFlow(router = router).switch() },
                        )
                      }
                    }
                    is HomeSection.Settings -> {
                      Box(modifier = Modifier.systemBarsPadding()) {
                        route.content.invoke()
                        BottomNavigationBar(
                          modifier = Modifier.align(Alignment.BottomCenter),
                          onPlacesClick = { HomeFlow(router = router, flowModule = app.applicationScope.appFlowModule).switch() },
                          onSettingsClick = {},
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
}

@Composable
fun BottomNavigationBar(
  modifier: Modifier = Modifier,
  onPlacesClick: () -> Unit,
  onSettingsClick: () -> Unit,
) {
  val iconSize = 24.dp

  Row(
    modifier =
      modifier
        .fillMaxWidth()
        .height(64.dp)
        .background(AppTheme.colors.backgroundPrimary)
        .padding(horizontal = 8.dp),
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(
      modifier = Modifier.clickable(onClick = onPlacesClick),
    ) {
      Icon(
        painter = painterResource(R.drawable.ic_home_24),
        contentDescription = null,
        tint = AppTheme.colors.accentPrimary,
        modifier = Modifier.size(iconSize),
      )
      Text(
        text = stringResource(R.string.places),
        style = AppTheme.typography.bodySmall,
        color = AppTheme.colors.accentPrimary,
        modifier = Modifier.padding(top = 4.dp),
        maxLines = 1,
      )
    }

    // Settings Tab
    Column(
      modifier = Modifier.clickable(onClick = onSettingsClick),
    ) {
      Icon(
        painter = painterResource(R.drawable.ic_settings_24),
        contentDescription = null,
        tint = AppTheme.colors.contentSecondary,
        modifier = Modifier.size(iconSize),
      )
      Text(
        text = stringResource(R.string.settings_title),
        style = AppTheme.typography.bodySmall,
        color = AppTheme.colors.contentSecondary,
        modifier = Modifier.padding(top = 4.dp),
        maxLines = 1,
      )
    }
  }
}

@Immutable
sealed interface HomeSection : Section {

  @Immutable
  data object Settings : HomeSection
  @Immutable

  data object Places : HomeSection
}
