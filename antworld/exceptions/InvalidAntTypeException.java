package antworld.exceptions;

public class InvalidAntTypeException extends Exception
{
  public InvalidAntTypeException(String message)
  {
    super(message + "is not a valid ant type");
  }
}
