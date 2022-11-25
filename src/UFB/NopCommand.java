class NopCommand implements GenericCommand{
  final int[] compiled;
  final StringBuilder errors=new StringBuilder();
  public NopCommand(final String[] line, final String[] realLine){
    compiled=new int[2];
    checkCases(line, realLine);
    compile(line);
  }
  @Override
  public void compile(final String[] line){
    compiled[0]=9;
  }
  @Override
  public int[] getCompiled(){
    return compiled;
  }
  @Override
  public void checkCases(final String[] line, final String[] realLine){
    if(line.length!=1)
      errors.append(Command.formatError(
        line, "Command", line[0],
        "Needs No Less And No More Than Three Arguments To Work"
      ));
  }
  @Override
  public String getErrors(){
    return errors.toString();
  }
}