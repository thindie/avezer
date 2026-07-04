# Project Instructions

## Planning before editing files

Before making any code changes — even simple ones — you must call `feature_planner` first to generate a plan. This is not optional: it ensures your edits are well-structured and correct the first time.

**The rule:** Always invoke `feature_planner` as the very first tool call when working on anything that involves editing files, regardless of how small or straightforward the task seems. Do not skip this step to "move faster."

## Build validation after changes

After completing a coding task, use `get_build_command` to run a build and verify your changes compile correctly before reporting success.

## Code review guidelines

- Read files with `read_file` before editing — never guess file contents or paths
- Update related tests, documentation, configuration, or call sites when they are part of the requested change
- Keep changes minimal and focused on the task at hand

# Code
- Indent is 2

## Git

- Do not commit or create new branches unless the user explicitly requests it
- When committing, follow good Git style: short subject line (≤50 chars), imperative mood, blank-line separator from body
- If user asks to commit changes, use this  simple but strict action sequence without skip
   1) check git status
   2) summarize the changes
   3) atomically separate the logic bounds
   4) check git history to resolve the style of commits at current project
   5) atomically and sequintionally commit those changes

## Examples

**Good plan structure:**
1. Identify affected files and dependencies. 
e.g. A.kt
changes: line 2 - 20
full path: <full path>
2. Design the change (functions, classes, or config to modify)
3. Implement with minimal edits
4. Validate via build

**Bad approach:** Jumping straight into editing without a plan — this often leads to missed edge cases or broken related code.
**The rule:** once plan is done, must proceed to implement without confirm. proceed with spawn_agent or directly. 


## Getting Job Done

1. after build complete, it should be good to run app:ktLintFormat
2. after all resolved, go checklist
 - what is the core functionality of the implemented feature
 - is it work by now as expected? 
    a. can UI render the updated functionality?
    b. if the stubs was added, can they be replaced and linked with existed codebase properly?
    c. is the code is just writted and project build, but the user cant see the real changes?
3. summarize, and additions is tiny and pretty logical - implement them. if does, repeat Getting Job Done