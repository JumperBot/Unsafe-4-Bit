---
# Feel free to add content and custom Front Matter to this file.
# To modify the layout, see https://jekyllrb.com/docs/themes/#overriding-theme-defaults

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
  .wavy {
		position: relative;
  }
  .wavy span {
		position: relative;
    display: inline-block;
    animation: animate 2.5s ease-in-out infinite;
    animation-delay: calc(0.1s * var(--i));
  }
  @keyframes animate {
		0% {
			transform: translate(0px, 0px);
    }
    25% {
      transform: translate(5px, -5px);
    }
    75% {
      transform: translate(-5px, 5px);
		}
  }
</style>

<br>

<h1 class="center_text"><a href="hello_world.html">HELLO, WORLD!</a></h1>

<br>

<h2>Let's start with something <i><b>comfortable</b></i>...</h2>

<pre>
<span class="command">print</span> <span class="string">"Hello, World!\n"</span>
</pre>

<h2>Have you <span class="wavy"><span style="--i:1">n</span><span style="--i:2">o</span><span style="--i:3">t</span><span style="--i:4">i</span><span style="--i:5">c</span><span style="--i:6">e</span><span style="--i:7">d</span></span>
something?</h2>

<blockquote>It's probably the ugly formatting...</blockquote>

<h3>It's the breeze of simplicity and the freedom of UFB code.</h3>
<h3><b>BUT</b> there's always another way to write something so simple.</h3>

<pre>
<span class="command">wvar</span> <span class="index"> 38</span> <span class="string">"Hello World!\n"</span>
<span class="command">print</span> <span class="index">38</span>

<span class="comment">// Or...</span>
<span class="comment">/&ast; Or... Oh... Boy... &ast;/</span>

<span class="command">print</span> <span class="index">8  5  12 12 15 0  23 15 18 12 4</span>
</pre>

<blockquote>My, my... there are hundreds of different ways to do this, honestly.</blockquote>

<h3>UFB is a fast, verbose and easy language.</h3>
<h3>If you find a situation hard...</h3>
<h3>then that only means that you made it hard for your own good.</h3>

<br>

<a href="index.html" class="arrows" style="float: left;">⏪️</a>
<a href="literacy.html" class="arrows" style="float: right;">⏩</a>
