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

<h1 class="center_text"><a href="math.html">MATH</a></h1>

<br>

<h2>Here, Mathematics becomes Math, Mat, Ma, or maybe M... yummy.</h2>

<blockquote>
We're not going to solve for the total area of one dispersed floating field of magnets here.
</blockquote>

<h3>We can always start with the basics... and dive into whatever is next.</h3>

<pre>
<span class="comment">// It's a recurring theme...</span>
<span class="comment">// all commands that can, will always place the output to the first given memory index.</span>

<span class="comment">// $038 + $250 | Addition</span>
<span class="command">add</span> <span class="index">38 250</span>

<span class="comment">// $038 - $250 | Subtraction</span>
<span class="command">sub</span> <span class="index">38 250</span>

<span class="comment">// $038 * $250 | Multiplication</span>
<span class="command">mul</span> <span class="index">38 250</span>

<span class="comment">// $038 / $250 | Division</span>
<span class="command">sub</span> <span class="index">38 250</span>

<span class="comment">// $038 % $250 | Modulus</span>
<span class="command">mod</span> <span class="index">38 250</span>

<span class="comment">// $038 / $250 (Take away the decimal numbers)| Reverse Modulus</span>
<span class="command">rmod</span> <span class="index">38 250</span>
</pre>

<br>

<a href="variables.html" class="arrows" style="float: left;">⏪️</a>
<a href="jump.html" class="arrows" style="float: right;">⏩</a>
