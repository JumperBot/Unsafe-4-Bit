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

class MathCommand implements GenericCommand{
  final int comInd;
  final int[] compiled;
  final StringBuilder errors=new StringBuilder();
  public MathCommand(final int comInd, final String[] line, final String[] realLine){
    this.comInd=comInd;
    compiled=new int[line.length];
    checkCases(line, realLine);
    compile(line);
  }
  @Override
  public void compile(final String[] line){
    compiled[0]=comInd;
    for(int i=1;i<line.length;i++)
      compiled[i]=Command.toIntAbsolute(line[i]);
  }
  @Override
  public int[] getCompiled(){
    return compiled;
  }
  @Override
  public void checkCases(final String[] line, final String[] realLine){
    if(line.length!=3)
      errors.append(Command.formatError(
        line, "Command", line[0],
        "Needs No Less And No More Than Three Arguments To Work"
      ));
    try{
      if(Long.parseLong(line[1])<38)
        errors.append(Command.formatError(
          line, "Memory Index", line[1],
          "Endangers A Read-Only Memory Index"
        ));
    }catch(final Exception e){
      errors.append(Command.formatError(
        line, "Memory Index Expected Instead Of", line[1],
        "Should Be Replaced With A Memory Index"
      ));
    }
    for(int i=1;i<line.length;i++)
      if(Long.parseLong(line[i])>255)
        try{
          errors.append(Command.formatError(
            line, "Memory Index", line[i],
            "Is Larger Than 255 And Will Not Point To Memory"
          ));
        }catch(final Exception e){
          errors.append(Command.formatError(
            line, "Memory Index Expected Instead Of", line[i],
            "Should Be Replaced With A Memory Index"
          ));
        }
  }
  @Override
  public String getErrors(){
    return errors.toString();
  }
}