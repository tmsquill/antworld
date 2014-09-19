package antworld.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import antworld.ant.Ant;
import antworld.astar.Location;
import antworld.client.Client;
import antworld.constants.ActivityEnum;
import antworld.info.AntManager;

public class AntControl extends JTabbedPane
{
  public AntControl(AntManager activeAnts)
  {
    this.addTab("Ant Commands", new AntCommand(activeAnts));
  }

  public class AntCommand extends JPanel implements ActionListener
  {
    private JButton returnToNestButton = this.createJButton("Return To Nest");
    private JButton forceRandomWalkButton = this.createJButton("Force Random Walk");

    public AntCommand(AntManager activeAnts)
    {
      this.setLayout(new BorderLayout());

      this.add(this.returnToNestButton, BorderLayout.NORTH);
      this.add(this.forceRandomWalkButton, BorderLayout.SOUTH);
    }

    private JButton createJButton(String buttonText)
    {
      final JButton button = new JButton(buttonText);
      button.addActionListener(this);
      return button;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
      Object event = e.getSource();
      if (event == this.returnToNestButton)
      {
        System.out.println("Returning all ants to the nest...");
        for (Ant ant : Client.getActiveAntManager().getAllMyAnts().values())
        {
          ant.setActivity(ActivityEnum.RETREATING);
          ant.getDirections().clear();
          ant.setDestination(new Location(Client.centerX, Client.centerY));
          ant.setDirections(Ant.astar.dispatchAStar(ant.getCurrentLocation(), ant.getDestination()));
        }
      }
      else if (event == this.forceRandomWalkButton)
      {
        System.out.println("Forcing all ants to take a random walk for 10 steps...");
        for (Ant ant : Client.getActiveAntManager().getAllMyAnts().values())
        {
          ant.setActivity(ActivityEnum.SEARCHING_FOR_RESOURCE);
          ant.getDirections().clear();
          ant.assignRandomDirections(10);
          ant.setDirections(Ant.astar.dispatchAStar(ant.getCurrentLocation(), ant.getDestination()));
        }
      }
    }
  }
}