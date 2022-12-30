<div align="center">

[![License Type Badge](https://img.shields.io/badge/license-GPL--3.0-blue?style=for-the-badge)]()
[![Version Tag  Badge](https://img.shields.io/github/v/release/JumperBot/Unsafe-4-Bit?color=green&style=for-the-badge)]()
[![Code Size    Badge](https://img.shields.io/github/languages/code-size/JumperBot/Unsafe-4-Bit?color=orange&style=for-the-badge)]()
[![Repo Size    Badge](https://img.shields.io/github/repo-size/JumperBot/Unsafe-4-Bit?color=red&style=for-the-badge)]()

---

[![Banner.png](./res/ShortenedBanner.png)](./res/ShortenedBanner.png)

[![UFBDesc.apng](./res/UFBDesc.apng)](./res/UFBDesc.apng)
---

> Unsafe Four Bit | UFB | Unsafe-4-Bit  
> Fast-Paced | Compiled-Interpreted | Dynamically-Typed | Imperative-Procedural  
> Programming Language Built With Rust.

---

UFB reminds you of its ***lower-level counterparts*** as you manage your ***"RAM"***.

The [**`256 items in memory`**](https://github.com/JumperBot/Unsafe-4-Bit/wiki/Z-Others#Memory) is divided into two parts: ROM and non-ROM.

This ***"freedom"*** lets you ***"shoot yourself in the foot"***.

</div>

---

<div align="center">

# :heavy_plus_sign: [**`Installation`**](./#heavy_plus_sign-installation-arrow_down) :arrow_down:

### [**`Download The Latest Binary`**](https://github.com/JumperBot/Unsafe-4-Bit/releases/latest)

### [**`Or Clone The Repository And Get Continous Updates`**](./#or-clone-the-repository-and-get-continous-updates):

```bash
git clone https://github.com/JumperBot/Unsafe-4-Bit.git
```

</div>

---

<div align="center">

# :construction: [**`Contributing`**](./res/CONTRIBUTING.md) :construction_worker:

|                                                    |     |                                                           |
|:---------------------------------------------------|:---:|----------------------------------------------------------:|
|Good at **designing** homepages?                    |     |You think that the code is ***slow***?                     |
|Try *redesigining* this README!                     |     |*Reconstruct* the code and open a [**`pull request`**][0]! |
|                                                    |     |                                                           |
|                                                    |     |                                                           |
|Want a **new feature** to be added?                 |     |Want to help in another way?                               |
|*Submit* an [**`issue`**][1] or *code* it yourself! |     |***Star*** the repository!                                 |
|                                                    |     |                                                           |

[0]: https://github.com/JumperBot/Unsafe-4-Bit/compare
[1]: https://github.com/JumperBot/Unsafe-4-Bit/issues/new/choose

</div>

---

<div align="center">

# :technologist: [**`Tutorial`**](https://github.com/JumperBot/Unsafe-4-Bit/wiki) :monocle_face:

### Write your first program full of [**`commands`**](https://github.com/JumperBot/Unsafe-4-Bit/wiki/Z-Others#default-16).

### 'Then [**`compile`**](https://github.com/JumperBot/Unsafe-4-Bit/releases/latest) and [**`run`**](https://github.com/JumperBot/Unsafe-4-Bit/releases/latest) the program!

</div>

---

<div align="center">

# :wrench: [**`Example`**](./#wrench-example-hammer) :hammer:

</div>

```python
// This program creates an endless background of "Hello World"s.

/*
Write "Hello World" to memory index: 38
*/
wvar  38  "Hello World "
// Print the variable residing in memory index: 38
print 38
/* Jump to command no. 0
   if the values in memory indexes 0 and 0 are equal */
je    0  0  0
// Empty out memory index 38 to avoid memory leakage
nvar  38
```
