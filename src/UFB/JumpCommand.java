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
      compiled[i]=Command.toIntAbsolute(line[i]);
    final short lineNum=(short)Command.toIntAbsolute(line[line.length-1]);
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
      errors.append(Command.formatError(
        line, "Command", line[0],
        "Needs No Less And No More Than Three Arguments To Work"
      ));
    for(int i=1;i<line.length-1;i++)
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
    if(Long.parseLong(line[line.length-1])>32767)
      try{
        errors.append(Command.formatError(
          line, "Command Number", line[line.length-1],
          "Is Larger Than 32767 And Will Not Point Accurately"
        ));
      }catch(final Exception e){
        errors.append(Command.formatError(
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