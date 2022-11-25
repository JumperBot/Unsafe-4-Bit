class WvarCommand extends NeedsArgLengthCommand{
  public WvarCommand(final String[] line, final String[] realLine){
    super(0, line, realLine);
  }
  @Override
  public void checkCases(final String[] line, final String[] realLine){
    super.checkCases(line, realLine);
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
  }
}