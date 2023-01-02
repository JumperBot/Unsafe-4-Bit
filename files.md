---
layout: default
---

<style>
	a {
    text-decoration: none;
		font-weight: bold;
	}
	.center_text {
		text-align: center;
	}
	.arrows {
		font-size: 50px;
	}
	.command {
		color: red;
	}
	.string {
		color: yellow;
	}
	.index {
		color: orange;
	}
	.comment {
		color: grey;
	}
</style>

<br>

<h1 class="center_text"><a href="files.html">FILES</a></h1>

<br>

<h2>Not all languages can access your files!.. Right?</h2>

<blockquote>
🚨 This part of the language may change in the future! <a href="https://github.com/JumperBot/Unsafe-4-Bit/issues/42">#42</a>
</blockquote>

<h3>In other languages, accessing the filesystem takes too many keystrokes.</h3>
<h4>You're quite lucky that it will only take you one line to read / write / delete a file!</h4>

<pre>
<span class="comment">// These commands WILL crash the program if they are unsuccessful, somehow.</span>

<span class="comment">// Write the data in memory index 38 into <span class="string">":PROGRAM_DIR:/foo.txt"</span>.</span>
<span class="comment">// Automatically create the file / subdirectories if they do not exist yet.</span>
<span class="command">wfile</span> <span class="index">38 </span><span class="string">"foo.txt"</span>

<span class="comment">// Write the data in <span class="string">":PROGRAM_DIR:/foo.txt"</span> into memory index 38.</span>
<span class="command">rfile</span> <span class="index">38 </span><span class="string">"foo.txt"</span>

<span class="comment">// Delete the file / directory <span class="string">":PROGRAM_DIR:/foo.txt"</span>.</span>
<span class="comment">// This will delete the child directories if it is a directory, so be careful!</span>
<span class="command">dfile</span> <span class="string">"foo.txt"</span>
</pre>

<blockquote>
🚨 This part of the language may change in the future! <a href="https://github.com/JumperBot/Unsafe-4-Bit/issues/42">#42</a>
</blockquote>

<br>

<a href="nothingness.html" class="arrows" style="float: left;">⏪️</a>
<a href="under_construction.html" class="arrows" style="float: right;">⏩</a>
