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

class JumpCommand implements GenericCommand{
  final int comInd;
  final int[] compiled;
  final StringBuilder errors=new StringBuilder();
  public JumpCommand(final int comInd, final String[] line, final String[] realLine){
    this.comInd=comInd;
    compiled=new int[line.length+1];
    checkCases(line, realLine);
    compile(line);
  }
  @Override
  public void compile(final String[] line){
    compiled[0]=comInd;
    for(int i=1;i<line.length;i++)
      compiled[i]=Universal.toIntAbsolute(line[i]);
    final short lineNum=(short)Universal.toIntAbsolute(line[line.length-1]);
    compiled[compiled.length-2]=lineNum>>>8;
    compiled[compiled.length-1]=lineNum<<8>>>8;
  }
  @Override
  public int[] getCompiled(){
    return compiled;
  }
  @Override
  public void checkCases(final String[] line, final String[] realLine){
    if(line.length!=4)
      errors.append(Universal.formatError(
        line, "Command", line[0],
        "Needs No Less And No More Than Three Arguments To Work"
      ));
    for(int i=1;i<line.length-1;i++)
      try{
        if(Long.parseLong(line[i])>255)
          errors.append(Universal.formatError(
            line, "Memory Index", line[i],
            "Is Larger Than 255 And Will Not Point To Memory"
          ));
      }catch(final Exception e){
        errors.append(Universal.formatError(
          line, "Memory Index Expected Instead Of", line[i],
          "Should Be Replaced With A Memory Index"
        ));
      }
    try{
      if(Long.parseLong(line[line.length-1])>32767)
        errors.append(Universal.formatError(
          line, "Command Number", line[line.length-1],
          "Is Larger Than 32767 And Will Not Point Accurately"
        ));
    }catch(final Exception e){
      errors.append(Universal.formatError(
        line, "Command Number Expected Instead Of", line[line.length-1],
        "Should Be Replaced With A Command Number"
      ));
    }
  }
  @Override
  public String getErrors(){
    return errors.toString();
  }
}