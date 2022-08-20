# UFB

`UFB.java` / `/UFB/*.class` is the interpreter itself.

Only files containing `.ufbb` will be interpreted.

## Usage

```sh
cd ../build/UFB/
java UFB [ -flags ] /directory/Compiled.ufbb
```

## Flags

- "-p" -> Performance measurement flag -> ms
- "-n" -> Accurately use the "-p" flag -> ns
- "-v" -> Display semantic version tag
- "-h" -> Display links and then leave

Example:

```sh
java UFB -pnv
java UFB -p
java UFB -nvp
java UFB -pnhv
```

# UFBC

`UFBC.java` / `UFBC.class` is the compiler itself.

Any file can be used as an input file.

Files may or may not be compiled at all due to syntax errors.

```sh
cd ../build/UFBC/
java UFBC /directory/NotCompiled.ufb
```
