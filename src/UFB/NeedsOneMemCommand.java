class NeedsOneMemCommand implements GenericCommand{
  final int comInd;
  final int[] compiled;
  final StringBuilder errors=new StringBuilder();
  public NeedsOneMemCommand(final int comInd, final String[] line, final String[] realLine){
    this.comInd=comInd;
    compiled=new int[2];
    checkCases(line, realLine);
    compile(line);
  }
  @Override
  public void compile(final String[] line){
    compiled[0]=comInd;
    compiled[1]=Command.toIntAbsolute(line[1]);
  }
  @Override
  public int[] getCompiled(){
    return compiled;
  }
  @Override
  public void checkCases(final String[] line, final String[] realLine){
    if(line.length!=2)
      errors.append(Command.formatError(
        realLine, "Command", line[0],
        "Has Too Many Arguments",
        Command.lineGen(line)
      ));
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