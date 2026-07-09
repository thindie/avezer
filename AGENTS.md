# Project Guidelines & Workflow

## 1. Planning Before Editing
- **Always invoke `feature_planner`** as the first tool call before any file modifications, regardless of task size. Do not skip this step to "move faster."
- Analyze affected files, dependencies, and required changes before implementation.
- Once a plan is approved, proceed immediately to implementation without additional confirmation.

## 2. Implementation Rules
- **Implementation:** If it is clear, that new functionality will be added, must check mcp tool Context7. If spawn subagents - Enforce this rule when spawning subagents.
- **Localisation:** If it is clear, that new resources/values will be added, must implement localisation changes at all resource files. If spawn subagents - Enforce this rule when spawning subagents.
- **Indentation:** Use 2-space indentation. If spawn subagents - Enforce this rule when spawning subagents.
- **Minimal Changes:** Keep edits focused on the task at hand. Update related tests, docs, configs, or call sites only if they are part of the change scope.

## 3. Validation & Code Quality
- **Build Verification:** After completing a task (via subagent or directly), run `get_build_command` to verify compilation before reporting success.
- **Code Review Guidelines:**
    - Read files with `read_file` before editing — never guess contents or paths.
    - Ensure changes pass linting and formatting checks.
- **Formatting:** After a successful build, run `app:ktlintFormat` to enforce consistent code style.

## 4. Git Workflow
- **No automatic commits/branches** unless explicitly requested by the user.
- **Commit Style (when requested):**
    - Short subject line (≤50 chars), imperative mood, blank-line separator from body.
    - Follow this strict sequence without skipping:
        1. `git status`
        2. Summarize changes
        3. Group related logic atomically
        4. Check git history for project commit conventions
        5. Commit sequentially and atomically

## 5. Final Checklist & Iteration ("Getting Job Done")
After all changes are implemented and validated:
- [ ] **Core Functionality:** What does this feature do?
- [ ] **Expected Behavior:** Does it work as intended?
    - Can the UI render the updated functionality?
    - Are stubs properly linked to existing codebase?
    - Is the code functional beyond just building? (User-visible changes?)
- [ ] **Summary & Additions:** If additional tiny/logical steps are needed, implement them and repeat this checklist.

---
### ⚡ Quick Pipeline Reference
`Plan → Implement → Build Verify → Format → Git (if requested) → Checklist → Iterate`
