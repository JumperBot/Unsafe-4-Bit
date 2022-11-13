<div align="center">

# UFB

`/UFB/*.class` | `UFB.jar` is the interpreter | compiler merged as one.

Only one file ending in `.ufbb` | `ufb` will be interpreted | compiled.

A file will not be compiled | interpreted if there are errors.

</div>

---

## Usage

```shell
java -jar UFB.jar [ -flags ] [ file ]
```

---

## Flags

- "-p" -> Performance measurement flag -> ms
- "-n" -> Accurately use the "-p" flag -> ns
- "-m" -> Time all commands being run
- "-v" -> Display semantic version tag
- "-h" -> Display help links and sources
- "-c" -> Compile file

---

## Example

```shell
java UFB -pnv
java UFB -p
java UFB -m
java UFB -nvp
java UFB -pnhv
java UFB -mn
java UFB -c ../test/UFB/Main.ufb
```