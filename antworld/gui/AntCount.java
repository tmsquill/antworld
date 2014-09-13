package antworld.gui;

public class AntCount
{
  private String type;
  private int    count;

  public AntCount(String type, int count)
  {
    this.type = type;
    this.count = count;
  }

  public String getType()
  {
    return this.type;
  }

  public int getCount()
  {
    return this.count;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public void setCount(int count)
  {
    this.count = count;
  }
}
