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
	//----------------------------------------------------------------------//
	/**TODO: ALWAYS CHANGE VERSION TAG.
	 * DO NOT Change '1' in "1.*.*".
	 * MINOR CHANGES should go in "1.MINOR.*".
	 * PATCH CHANGES should go in "1.*.PATCH".
	 * MINOR CHANGES should give new commands/major features.
	 * PATCH CHANGES should give new flags/performance-boosts/bug-fixes/etc.
	**/
	final static String version_tag="v1.1.3";
	//----------------------------------------------------------------------//
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
    final String fileName=flagManager.getFileName();
    if(fileName.length()==0){
      System.out.println("No file input found, terminating.");
      System.exit(1);
      return;
    }
    if(flagManager.isFlagActivated('c')){
      if(fileName.endsWith(".ufbb")){
        System.out.println("Could not compile an already compiled source code.");
        System.out.println("Remove the compilation flag to run the compiled program.");
        return;
      }
      new UFBC(fileName);
      return;
    }
    if(fileName.endsWith(".ufb")){
      System.out.println("Could not run uncompiled source code.");
      System.out.println("Add the compilation flag to compile the program.");
      return;
    }
    new Runner(
      fileName,
      flagManager.isFlagActivated('p'),
      flagManager.isFlagActivated('n'),
      flagManager.isFlagActivated('m')
    );
	}
}