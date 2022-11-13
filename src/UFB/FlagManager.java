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

import java.util.Arrays;

import java.util.regex.Pattern;

class FlagManager{
  final boolean[] isActivated=new boolean[6];
  final String file;
  final Pattern flags=Pattern.compile("[pnmhvc]");
  final Pattern repeats=Pattern.compile("(\\w)\\1+");
  public FlagManager(final String[]a){
    String fileName="";
		for(final String s:a){
			final String arg=s.trim();
			if(arg.endsWith(".ufbb")||arg.endsWith(".ufb"))fileName=arg;
			else if(arg.startsWith("-")){
        final String str=repeats.matcher(arg.replace("-", "")).replaceAll("$1");
        final String shouldBeEmpty=flags.matcher(str).replaceAll("");
        if(shouldBeEmpty.length()!=0){
          final String joined=Arrays.toString(shouldBeEmpty.split("")).substring(1);
          System.out.printf(
            "Unrecognized flags found: %s\n%s\n\n",
            joined.substring(0, joined.length()-1),
            "Continuing anyway..."
          );
        }
				if(str.contains("p"))isActivated[0]=true;
				if(str.contains("n")){
					isActivated[0]=true;
					isActivated[1]=true;
				}
				if(str.contains("m")){
					isActivated[0]=true;
					isActivated[2]=true;
				}
				if(str.contains("v"))isActivated[3]=true;
				if(str.contains("h"))isActivated[4]=true;
				if(str.contains("c"))isActivated[5]=true;
			}else System.out.printf(
        "Unrecognized argument: %s\nContinuing anyway...\n\n", arg
      );
		}
    file=fileName;
  }
  boolean isFlagActivated(final char c){
    switch(c){
      case 'p':
        return isActivated[0];
      case 'n':
        return isActivated[1];
      case 'm':
        return isActivated[2];
      case 'v':
        return isActivated[3];
      case 'h':
        return isActivated[4];
      case 'c':
        return isActivated[5];
    }
    return false;
  }
  String getFileName(){
    return file;
  }
}