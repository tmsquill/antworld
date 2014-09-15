package antworld.data;

public enum FoodType 
{
  UNKNOWN  {public int getColor() {return 0x0000C0;}},
  WATER    {public int getColor() {return 0x0000C8;}},
  DEFENCE  {public int getColor() {return 0xAA00FF;}},
  ATTACK   {public int getColor() {return 0xA300F4;}}, 
  SPEED    {public int getColor() {return 0x9800E3;}},
  VISION   {public int getColor() {return 0x8E00D5;}}, 
  CARRY    {public int getColor() {return 0x8900CD;}},
  MEDIC    {public int getColor() {return 0x7C00BA;}},
  BASIC    {public int getColor() {return 0x7000A8;}};
  
  public abstract int getColor();
  public static FoodType identifyTypeByColor(int rgb)
  {
    if (rgb == WATER.getColor()) return WATER;
    if (rgb == DEFENCE.getColor()) return DEFENCE;
    if (rgb == ATTACK.getColor()) return ATTACK;
    if (rgb == SPEED.getColor()) return SPEED;
    if (rgb == VISION.getColor()) return VISION;
    if (rgb == CARRY.getColor()) return CARRY;
    if (rgb == MEDIC.getColor()) return MEDIC;
    if (rgb == BASIC.getColor()) return BASIC;
    return null;
  }
  
  public static final int SIZE = values().length;
};  