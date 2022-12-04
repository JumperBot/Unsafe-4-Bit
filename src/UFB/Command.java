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

import java.util.ArrayList;
import java.util.HashMap;

import java.util.concurrent.ExecutorService;

class Command{
  final ArrayList<Integer> compiled=new ArrayList<>();
  final StringBuilder errors=new StringBuilder();
  final ExecutorService executor;
  final HashMap<String, Integer> binaryMap;
  final String[] line;
  final String[] realLine;
  boolean cancelOptimization=false;
  public boolean cancelOptimization(){
    return cancelOptimization;
  }
  public int[] getCompiled()throws Exception{
    final int[] returnMe=new int[compiled.size()];
    for(int i=0;i<returnMe.length;i++)
      returnMe[i]=compiled.get(i).intValue();
    return returnMe;
  }
  public String getErrors(){
    return errors.toString();
  }

  private Command(
    final String[] line, final String[] realLine,
    final ExecutorService executor, final HashMap<String, Integer> binaryMap
  ){
    this.line=line;
    this.realLine=realLine;
    this.executor=executor;
    this.binaryMap=binaryMap;
    this.line[0]=this.line[0].toLowerCase();
  }
  private void compile(){
    executor.execute(new Runnable(){
      public void run(){
        try{
          final GenericCommand com=getCommand(binaryMap.get(line[0]));
          errors.append(com.getErrors());
          for(final int i:com.getCompiled())
            compiled.add(i);
        }catch(final NullPointerException e){
          errors.append(Universal.formatError(
            line, "Command", line[0],
            "Does Not Exist"
          ));
        }
      }
    });
  }
  private GenericCommand getCommand(final int comInd){
    switch(comInd){
      case 0:
        return new WvarCommand(line, realLine);
      case 2:
        return new TrimCommand(line, realLine);
      case 3: case 4: case 5: case 6: case 7: case 8:
        return new MathCommand(comInd, line, realLine);
      case 9:
        return new NopCommand(line, realLine);
      case 10: case 11: case 12: case 13:
        return new JumpCommand(comInd, line, realLine);
      case 14:
        return new PrintCommand(line, realLine);
      case 15:
        cancelOptimization=true;
      case 1:
        return new NeedsOneMemCommand(comInd, line, realLine);
      case 17:
        cancelOptimization=true;
        return new RfileCommand(line, realLine);
      case 18:
      case 16:
        cancelOptimization=true;
        return new NeedsArgLengthCommand(comInd, line, realLine);
      default:
        return null;
    }
  }
  public static Command create(
    final String[] line, final String[] realLine,
    final ExecutorService executor, final HashMap<String, Integer> binaryMap
  ){
    final Command com=new Command(line, realLine, executor, binaryMap);
    com.compile();
    return com;
  }
}