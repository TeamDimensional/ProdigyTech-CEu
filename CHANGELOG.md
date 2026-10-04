# Changelog

## [1.3.7] - 2026-10-04

### Modpacks
- Magmatic Aeroheater now can use custom fluids, added through Groovyscript or Crafttweaker, and they can be optionally consumed

### Tweaks
- Food items giving 0 food points can no longer be input into the Food Purifier
    - Consequently, fixes an error with JEI handlers if such an item was found

## [1.3.6] - 2026-09-06

### Tweaks
- Food Purifier now has a JEI handler
- Purified Food now displays a tooltip whether it can be increased further

### Modpacks
- Solderer recipes now support custom circuit boards

### Bugfixes
- Fixed several issues with the Infusion functionality

## [1.3.5] - 2026-08-30

### Modpacks
- Solderer and Atomic Reshaper now support custom infusions, which can be added through Groovyscript and Crafttweaker

### QOL
- Incinerator no longer displays every item in the game as its input in JEI
- Improved JEI recipe rendering in various categories

### Bugfixes
- Incinerator will no longer cease to give output if its output is replaced by Unidict

### Miscellanous
- Migrated to RetroFuturaGradle
