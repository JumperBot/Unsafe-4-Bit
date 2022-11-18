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
  final String flagString="[pnmvhclb]";
  final String[] longFlagsArr={
    "unoptimized"
  };
  final boolean[] isActivated=new boolean[flagString.length()-2];
  final boolean[] isLongActivated=new boolean[longFlagsArr.length];
  final String file;
  public FlagManager(final String[]a){
    final Pattern flags=Pattern.compile(flagString);
    final Pattern longFlags=Pattern.compile(
      Arrays.toString(longFlagsArr).substring(1)
                                   .replaceAll("\\]$", "")
                                   .replace(", ", "|")
    );
    final Pattern repeats=Pattern.compile("(\\w)\\1+");
    final Pattern doubleHyphens=Pattern.compile("^-+");
    String fileName="";
		for(final String s:a){
			final String arg=s.trim();
			if(arg.endsWith(".ufbb")||arg.endsWith(".ufb"))fileName=arg;
      else if(arg.startsWith("--")){
        final String arg2=doubleHyphens.matcher(arg).replaceAll("");
        if(longFlags.matcher(arg2).matches()){
          for(int i=0;i<longFlagsArr.length;i++)
            if(longFlagsArr[i].equals(arg2))
              isLongActivated[i]=true;
        }else
          System.out.printf(
            "Unrecognized flags found: %s\n%s\n\n",
            arg2,
            "Continuing anyway..."
          );
      }else if(arg.startsWith("-")){
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
        for(int i=1;i<3;i++)
          if(str.contains(flagString.charAt(i+1)+""))
            isActivated[0]=isActivated[i]=true;
        for(int i=3;i<flagString.length()-2;i++)
          if(str.contains(flagString.charAt(i+1)+""))
            isActivated[i]=true;
			}else System.out.printf(
        "Unrecognized argument: %s\nContinuing anyway...\n\n", arg
      );
		}
    file=fileName;
  }
  public boolean isFlagActivated(final char c){
    final char[] array=flagString.substring(1).toCharArray();
    for(int i=0;i<array.length-1;i++)
      if(array[i]==c)
        return isActivated[i];
    return false;
  }
  public boolean isLongFlagActivated(final String s){
    for(int i=0;i<longFlagsArr.length;i++)
      if(longFlagsArr[i].equals(s))
        return isLongActivated[i];
    return false;
  }
  public String getFileName(){
    return file;
  }
}