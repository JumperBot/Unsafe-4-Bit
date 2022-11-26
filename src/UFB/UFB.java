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
    if(flagManager.isFlagActivated('v'))
      //----------------------------------------------------------------------//
      /**TODO: ALWAYS CHANGE SEMANTIC VERSION BEFORE RELEASING.
       * DO NOT Change '1' in "1.*.*".
       * MINOR CHANGES should go in "1.MINOR.*".
       * PATCH CHANGES should go in "1.*.PATCH".
       * MINOR CHANGES should give new commands/major features.
       * PATCH CHANGES should give new flags/performance-boosts/bug-fixes/etc.
      **/
      //----------------------------------------------------------------------//
      System.out.printf(
        "UFB version: %s (master)\n%s\n\n",
        "v1.4.1",
        "Flag triggered, continuing anyway..."
      );
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
    if(flagManager.isFlagActivated('l'))
      System.out.printf(
        "%s\n%s\n%s\n%s\n%s\n\n",
        "----------------------------------------------------------------------------------\n",
        new StringBuilder("GNU GENERAL PUBLIC LICENSE - Version 3, 29 June 2007\n\n")
        .append("Unsafe Four Bit is a compiled-interpreted, dynamically-typed programming language.\n")
        .append("Copyright (C) 2022  JumperBot_\n")
        .append("\n")
        .append("This program is free software: you can redistribute it and/or modify\n")
        .append("it under the terms of the GNU General Public License as published by\n")
        .append("the Free Software Foundation, either version 3 of the License, or\n")
        .append("(at your option) any later version.\n")
        .append("\n")
        .append("This program is distributed in the hope that it will be useful,\n")
        .append("but WITHOUT ANY WARRANTY; without even the implied warranty of\n")
        .append("MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n")
        .append("GNU General Public License for more details.\n")
        .append("\n")
        .append("You should have received a copy of the GNU General Public License\n")
        .append("along with this program.  If not, see <https://www.gnu.org/licenses/>.\n").toString(),
        "The copy of the license can be viewed at the root of this repository.\n",
        "----------------------------------------------------------------------------------\n",
        "Flag triggered, continuing anyway..."
      );
    final String fileName=flagManager.getFileName();
    if(flagManager.isFlagActivated('c')){
      if(fileName.endsWith(".ufbb")){
        System.out.printf(
          "%s\n%s\n",
          "Could not compile an already compiled source code.",
          "Remove the compilation flag to run the compiled program."
        );
        System.exit(1);
      }
      new UFBC().compile(fileName, !flagManager.isLongFlagActivated("unoptimized"));
      return;
    }
    if(fileName.endsWith(".ufb")){
      System.out.printf(
        "%s\n%s\n",
        "Could not run uncompiled source code.",
        "Add the compilation flag to compile the program."
      );
      System.exit(1);
    }
    new Runner(
      fileName,
      flagManager.isFlagActivated('p'),
      flagManager.isFlagActivated('n'),
      flagManager.isFlagActivated('m'),
      flagManager.isFlagActivated('b')
    ).run();
	}
}