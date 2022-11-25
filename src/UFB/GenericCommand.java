interface GenericCommand{
  public void compile(final String[] line);
  public int[] getCompiled();
  public void checkCases(final String[] line, final String[] realLine);
  public String getErrors();
}