# Commands

## Default-16

#### :bricks: nop (No Operation) :arrow_down:
```python
// This will make your program stop for 10 milliseconds!
nop
```
---
#### :pencil2: wvar (Write Variable) :arrow_down:
```python
// Write the data "Hello World" into memory index 38
wvar  38  "Hello World"
```
---
#### :lock: nvar (Null Variable) :arrow_down:
```python
// Remove data in memory index 38
nvar  38
// This avoids theoretical memory leaks
```
---
#### :fire: trim (Trim Memory) :arrow_down:
```python
// Trim data in memory index 38 ("Hello World") into "Hello"
// In other words, trim it to only fill in 5 bytes
trim  38  5
```
---
#### :memo: read (Read Console) :arrow_down:
```python
// Read user input and write it into memory index 38
read  38
```
---
#### :loud_sound: print (Print Data) :arrow_down:
```python
// Print data in memory index 38 into the console
print  38
```
---
#### :see_no_evil: math (Do Math) :arrow_down:
```python
// Add the data in memory index 38 by the data in memory index 39
add  38  39

// Sub the data in memory index 38 by the data in memory index 39
sub  38  39

// Mul the data in memory index 38 by the data in memory index 39
mul  38  39

// Div the data in memory index 38 by the data in memory index 39
div  38  39

// Div the data in memory index 39 by the data in memory index 38
// And then return the right-hand side
// From the decimal's perspective as the result
mod  38  39

// Div the data in memory index 39 by the data in memory index 38
// And then return the left-hand side
// From the decimal's perspective as the result
rmod  38  39


// NOTE: the result will always be stored in the first memory index
```
---
#### :twisted_rightwards_arrows: jump (Jump Statement) :arrow_down:
```python
// Jump to command number 0
// Only if the data in memory index 1 is more than the latter
jm   1   2   0

// Jump to command number 0
// Only if the data in memory index 1 is less than the latter
jl   1   2   0

// Jump to command number 0
// Only if the data in memory index 1 is equal to the latter
je   1   2   0

// Jump to command number 0
// Only if the data in memory index 1 is not equal to the latter
jne  1   2   0
```

---

### More Info

- `command no` doesn't know what a *"line"* is.

- `-|, \t` can divide between a command and its argument(s).

- All non-allowed characters are neglected by the compiler.

- Only the compiler will stop you from writing to the `ROM`.

- Manually compiling to a `.ufbb` file is NOT recommended.

---

# Memory

|Index	|Values		|ROM?	|
|:-----:|:-------:|:---:|
|0			|' '			|Yes	|
|1-26		|'A'-'Z'	|Yes	|
|27-36	|'0'-'9'	|Yes	|
|37			|'\n'			|Yes	|
|38-255	|'\u0000'	|No		|

---

# Comments

|Comment|Expanded Meaning		|Multi-line?	|
|:-----:|:-----------------:|:-----------:|
|//			|Normal Comment			|No						|
|/\*\*/	|Multi-line Comment	|Yes					|

---

# Example

```python
// This program creates an endless background of "Hello World"s.

/*
Write "Hello World" to memory index: 38
*/
wvar  38  "Hello World"
// Print the variable residing in memory index: 38
print 38
/* Jump to command no. 0
   if the values in memory indexes 0 and 0 are equal */
je    0  0  0
// Empty out memory index 38 to avoid memory leakage
nvar  38
```
