<div align="center">

[![License Type Badge](https://img.shields.io/badge/license-GPL--3.0-blue?style=for-the-badge)]()
[![Version Tag  Badge](https://img.shields.io/github/v/release/JumperBot/Unsafe-4-Bit?color=green&style=for-the-badge)]()
[![Code Size    Badge](https://img.shields.io/github/languages/code-size/JumperBot/Unsafe-4-Bit?color=orange&style=for-the-badge)]()
[![Repo Size    Badge](https://img.shields.io/github/repo-size/JumperBot/Unsafe-4-Bit?color=red&style=for-the-badge)]()

---

[![Banner.png](./ShortenedBanner.png)](./ShortenedBanner.png)

[![UFBDesc.apng](UFBDesc.apng)](UFBDesc.apng)
---

UFB reminds you of its ***lower-level counterparts*** as you manage your ***"RAM"***.

The [256 bytes of memory](./test/README.md#memory) is divided into two parts: ROM and non-ROM.

This ***"freedom"*** lets you ***"shoot yourself in the foot"***.

</div>

---

# <div align="center">:heavy_plus_sign: Installation :arrow_down:</div>

Download the archived .zip file of the **current** version:

https://github.com/JumperBot/Unsafe-4-Bit/archive/master.zip

Or clone the repository and get continous updates:

```python
git clone https://github.com/JumperBot/Unsafe-4-Bit.git
```

---

# <div align="center">:technologist: Tutorial :monocle_face:</div>

Write your first program full of [commands](./test/README.md#default-16), warnings and errors...

and [compile](./src/README.md#UFBC) and [run](./src/README.md#UFB) the program!

Example:

```python
// This program creates an endless background of "Hello World"s.

/*
Write "Hello World" to memory index: 38
*/
wvar  38  8  5  12  12  15  0  23  15  18  12  4
// Print the variable residing in memory index: 38
print 38
/* Jump to command no. 0
   if the values in memory indexes 0 and 0 are equal */
je    0  0  0
// Empty out memory index 38 to avoid memory leakage
nvar  38
```
