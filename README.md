<div align="center">

[![License Type Badge](https://img.shields.io/badge/license-GPL--3.0-blue?style=for-the-badge)]()
[![Version Tag  Badge](https://img.shields.io/github/v/release/JumperBot/Unsafe-4-Bit?color=green&style=for-the-badge)]()
[![Code Size    Badge](https://img.shields.io/github/languages/code-size/JumperBot/Unsafe-4-Bit?color=orange&style=for-the-badge)]()
[![Repo Size    Badge](https://img.shields.io/github/repo-size/JumperBot/Unsafe-4-Bit?color=red&style=for-the-badge)]()

---

[![Banner.png](./ShortenedBanner.png)](./ShortenedBanner.png)

[![UFBDesc.apng](UFBDesc.apng)](UFBDesc.apng)
---

> Unsafe Four Bit / UFB / Unsafe-4-Bit is a fast-paced, compiled-interpreted, dynamically-typed, imperative-procedural programming language built on top of the Java programming language.

---

UFB reminds you of its ***lower-level counterparts*** as you manage your ***"RAM"***.

The [256 bytes of memory](./test/README.md#memory) is divided into two parts: ROM and non-ROM.

This ***"freedom"*** lets you ***"shoot yourself in the foot"***.

</div>

---

<div align="center">

# :heavy_plus_sign: Installation :arrow_down:

Download the ***latest*** `.jar` file:

https://github.com/JumperBot/Unsafe-4-Bit/blob/master/build/UFB.jar

Or clone the repository and get continous updates:

```bash
git clone https://github.com/JumperBot/Unsafe-4-Bit.git
```

</div>

---

<div align="center">

# :construction: Contributing :construction_worker:

> Good at **designing** homepages?

Try *redesigining* this README!

---

> You think that the code is ***slow***?

*Reconstruct* the code and open a [pull request](https://github.com/JumperBot/Unsafe-4-Bit/compare)!

---

> Want a **new feature** to be added?

*Submit* an [issue](https://github.com/JumperBot/Unsafe-4-Bit/issues/new/choose) or *code* it yourself!

</div>

---

<div align="center">

# :technologist: Tutorial :monocle_face:

Write your first program full of [commands](./test/README.md#default-16), warnings and errors...

and [compile](./src/README.md#UFBC) and [run](./src/README.md#UFB) the program!

</div>

---

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
