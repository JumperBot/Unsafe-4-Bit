/**
 *
 *	Unsafe Four Bit is a compiled-interpreted, dynamically-typed programming language.
 *	Copyright (C) 2022  JumperBot_
 *
 *	This program is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
**/

class UFB{
	public static void main(final String[]a)throws Exception{
    final FlagManager flagManager=new FlagManager(a);
    if(flagManager.isFlagActivated('v')){
      System.out.printf(
        "UFB version: %s (master)\n%s\n\n",
        version_tag,
        "Flag triggered, continuing anyway..."
      );
    }
    if(flagManager.isFlagActivated('h')){
      final String repo="https://github.com/JumperBot/Unsafe-4-Bit";
      final String master="/tree/master/";
      System.out.printf(
        "%s\n%s\n%s%s%s\n%s%s%s\n%s\n%s\n%s\n\n",
        "Need help? Either visit these links:",
        repo,
        repo, master, "src#ufb",
        repo, master, "test#commands",
        "or visit the examples in the 'test' folder...",
        "only if you fully cloned the repository.",
        "Flag triggered, continuing anyway..."
      );
    }
		new Runner(a);
	}
}