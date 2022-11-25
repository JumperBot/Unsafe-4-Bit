class TrimCommand implements GenericCommand{
  final int[] compiled;
  final StringBuilder errors=new StringBuilder();
  public TrimCommand(final String[] line, final String[] realLine){
    compiled=new int[3];
    checkCases(line, realLine);
    compile(line);
  }
  @Override
  public void compile(final String[] line){
    compiled[0]=2;
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
    if(Long.parseLong(line[1])>255)
      try{
        errors.append(Command.formatError(
          line, "Memory Index", line[1],
          "Is Larger Than 255 And Will Not Point To Memory"
        ));
      }catch(final Exception e){
        errors.append(Command.formatError(
          line, "Memory Index Expected Instead Of", line[1],
          "Should Be Replaced With A Memory Index"
        ));
      }
    if(Long.parseLong(line[1])<38)
      try{
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
    if(Long.parseLong(line[2])>255)
      try{
        errors.append(Command.formatError(
          line, "Trim Length", line[2],
          "Is Larger Than 255 And Will Not Be Compiled Properly"
        ));
      }catch(final Exception e){
        errors.append(Command.formatError(
          line, "Trim Length Expected Instead Of", line[2],
          "Should Be Replaced With A Trim Length"
        ));
      }
  }
  @Override
  public String getErrors(){
    return errors.toString();
  }
}