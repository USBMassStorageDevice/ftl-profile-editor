package net.blerf.ftl.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.parser.SavedGameParser;
import net.blerf.ftl.ui.FieldEditorPanel;
import net.blerf.ftl.ui.FTLFrame;
import net.blerf.ftl.ui.StatusbarMouseListener;
import net.blerf.ftl.xml.ShipBlueprint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SavedGameGeneralPanel extends JPanel {

	private static final Logger log = LogManager.getLogger(SavedGameGeneralPanel.class);

	private static final String TOTAL_SHIPS_DEFEATED = "Total Ships Defeated";
	private static final String TOTAL_BEACONS = "Total Beacons";
	private static final String TOTAL_SCRAP = "Total Scrap";
	private static final String TOTAL_CREW_HIRED = "Total Crew Hired";
	private static final String ALPHA = "Alpha?";
	private static final String DIFFICULTY_EASY = "Easy Difficulty";

	private static final String SECTOR_LAYOUT_SEED = "Sector Layout Seed";
	private static final String REBEL_FLEET_OFFSET = "Rebel Fleet Offset";
	private static final String REBEL_FLEET_FUDGE = "Rebel Fleet Fudge";
	private static final String REBEL_PURSUIT_MOD = "Rebel Pursuit Mod";
	private static final String PLAYER_BEACON = "Player BeaconId";
	private static final String HAZARDS_VISIBLE = "Hazards Visible";

	private FTLFrame frame;
	private FieldEditorPanel sessionPanel = null;
	private FieldEditorPanel sectorPanel = null;

	public SavedGameGeneralPanel( FTLFrame frame ) {
		this.setLayout( new GridBagLayout() );

		this.frame = frame;

		sessionPanel = new FieldEditorPanel( true );
		sessionPanel.setBorder( BorderFactory.createTitledBorder("Session") );
		sessionPanel.addRow( TOTAL_SHIPS_DEFEATED, FieldEditorPanel.ContentType.INTEGER );
		sessionPanel.addRow( TOTAL_BEACONS, FieldEditorPanel.ContentType.INTEGER );
		sessionPanel.addRow( TOTAL_SCRAP, FieldEditorPanel.ContentType.INTEGER );
		sessionPanel.addRow( TOTAL_CREW_HIRED, FieldEditorPanel.ContentType.INTEGER );
		sessionPanel.addRow( ALPHA, FieldEditorPanel.ContentType.INTEGER );
		sessionPanel.addRow( DIFFICULTY_EASY, FieldEditorPanel.ContentType.BOOLEAN );
		sessionPanel.addBlankRow();

		sessionPanel.getInt(ALPHA).addMouseListener( new StatusbarMouseListener(frame, "Unknown session field. Always 0?") );
		sessionPanel.getBoolean(DIFFICULTY_EASY).addMouseListener( new StatusbarMouseListener(frame, "Uncheck for normal difficulty.") );

		sectorPanel = new FieldEditorPanel( true );
		sectorPanel.setBorder( BorderFactory.createTitledBorder("Sector") );
		sectorPanel.addRow( SECTOR_LAYOUT_SEED, FieldEditorPanel.ContentType.INTEGER );
		sectorPanel.getInt(SECTOR_LAYOUT_SEED).setDocument( new RegexDocument("-?[0-9]*") );
		sectorPanel.addRow( REBEL_FLEET_OFFSET, FieldEditorPanel.ContentType.INTEGER );
		sectorPanel.getInt(REBEL_FLEET_OFFSET).setDocument( new RegexDocument("-?[0-9]*") );
		sectorPanel.addRow( REBEL_FLEET_FUDGE, FieldEditorPanel.ContentType.INTEGER );
		sectorPanel.getInt(REBEL_FLEET_FUDGE).setDocument( new RegexDocument("-?[0-9]*") );
		sectorPanel.addRow( REBEL_PURSUIT_MOD, FieldEditorPanel.ContentType.INTEGER );
		sectorPanel.getInt(REBEL_PURSUIT_MOD).setDocument( new RegexDocument("-?[0-9]*") );
		sectorPanel.addRow( PLAYER_BEACON, FieldEditorPanel.ContentType.INTEGER );
		sectorPanel.addRow( HAZARDS_VISIBLE, FieldEditorPanel.ContentType.BOOLEAN );
		sectorPanel.addBlankRow();

		sectorPanel.getInt(SECTOR_LAYOUT_SEED).addMouseListener( new StatusbarMouseListener(frame, "A per-sector constant that seeds the random generation of the map, events, etc. (potentially dangerous).") );
		sectorPanel.getInt(REBEL_FLEET_OFFSET).addMouseListener( new StatusbarMouseListener(frame, "A large negative var (-750,-250,...,-n*25, approaching 0) + fudge = the fleet circle's edge.") );
		sectorPanel.getInt(REBEL_FLEET_FUDGE).addMouseListener( new StatusbarMouseListener(frame, "A random per-sector constant (usually around 75-310) + offset = the fleet circle's edge.") );
		sectorPanel.getInt(REBEL_PURSUIT_MOD).addMouseListener( new StatusbarMouseListener(frame, "Delay/alert the fleet, changing the warning zone thickness (e.g., merc distraction = -2).") );
		sectorPanel.getInt(PLAYER_BEACON).addMouseListener( new StatusbarMouseListener(frame, "A 0-based index (0 to 23ish) of the player ship's beacon, counting down the map in columns, left-to-right.") );
		sectorPanel.getBoolean(HAZARDS_VISIBLE).addMouseListener( new StatusbarMouseListener(frame, "Show hazards on the current sector map.") );

		GridBagConstraints thisC = new GridBagConstraints();
		thisC.fill = GridBagConstraints.NONE;
		thisC.weightx = 0.0;
		thisC.weighty = 0.0;
		thisC.gridx = 0;
		thisC.gridy = 0;
		this.add( sessionPanel, thisC );

		thisC.gridy++;
		this.add( sectorPanel, thisC );

		thisC.fill = GridBagConstraints.BOTH;
		thisC.weighty = 1.0;
		thisC.gridx = 0;
		thisC.gridy++;
		this.add( Box.createVerticalGlue(), thisC );

		setGameState( null );
	}

	public void setGameState( SavedGameParser.SavedGameState gameState ) {
		sessionPanel.reset();
		sectorPanel.reset();

		if ( gameState != null ) {
			SavedGameParser.ShipState shipState = gameState.getPlayerShipState();
			ShipBlueprint shipBlueprint = DataManager.get().getShip( shipState.getShipBlueprintId() );
			if ( shipBlueprint == null )
				throw new RuntimeException( String.format("Could not find blueprint for%s ship: %s", (shipState.isAuto() ? " auto" : ""), shipState.getShipName()) );

			sessionPanel.setIntAndReminder( TOTAL_SHIPS_DEFEATED, gameState.getTotalShipsDefeated() );
			sessionPanel.setIntAndReminder( TOTAL_BEACONS, gameState.getTotalBeaconsExplored() );
			sessionPanel.setIntAndReminder( TOTAL_SCRAP, gameState.getTotalScrapCollected() );
			sessionPanel.setIntAndReminder( TOTAL_CREW_HIRED, gameState.getTotalCrewHired() );
			sessionPanel.setIntAndReminder( ALPHA, gameState.getHeaderAlpha() );
			sessionPanel.setBoolAndReminder( DIFFICULTY_EASY, gameState.isDifficultyEasy() );

			sectorPanel.setIntAndReminder( SECTOR_LAYOUT_SEED, gameState.getSectorLayoutSeed() );
			sectorPanel.setIntAndReminder( REBEL_FLEET_OFFSET, gameState.getRebelFleetOffset() );
			sectorPanel.setIntAndReminder( REBEL_FLEET_FUDGE, gameState.getRebelFleetFudge() );
			sectorPanel.setIntAndReminder( REBEL_PURSUIT_MOD, gameState.getRebelPursuitMod() );
			sectorPanel.setIntAndReminder( PLAYER_BEACON, gameState.getCurrentBeaconId() );
			sectorPanel.setBoolAndReminder( HAZARDS_VISIBLE, gameState.areSectorHazardsVisible() );
		}

		this.repaint();
	}

	public void updateGameState( SavedGameParser.SavedGameState gameState ) {
		SavedGameParser.ShipState shipState = gameState.getPlayerShipState();
		String newString = null;

		newString = sessionPanel.getInt(TOTAL_SHIPS_DEFEATED).getText();
		try { gameState.setTotalShipsDefeated(Integer.parseInt(newString)); }
		catch (NumberFormatException e) {}

		newString = sessionPanel.getInt(TOTAL_BEACONS).getText();
		try { gameState.setTotalBeaconsExplored(Integer.parseInt(newString)); }
		catch (NumberFormatException e) {}

		newString = sessionPanel.getInt(TOTAL_SCRAP).getText();
		try { gameState.setTotalScrapCollected(Integer.parseInt(newString)); }
		catch (NumberFormatException e) {}

		newString = sessionPanel.getInt(TOTAL_CREW_HIRED).getText();
		try { gameState.setTotalCrewHired(Integer.parseInt(newString)); }
		catch (NumberFormatException e) {}

		newString = sessionPanel.getInt(ALPHA).getText();
		try { gameState.setHeaderAlpha(Integer.parseInt(newString)); }
		catch (NumberFormatException e) {}

		gameState.setDifficultyEasy( sessionPanel.getBoolean(DIFFICULTY_EASY).isSelected() );

		newString = sectorPanel.getInt(SECTOR_LAYOUT_SEED).getText();
		try { gameState.setSectorLayoutSeed(Integer.parseInt(newString)); }
		catch (NumberFormatException e) {}

		newString = sectorPanel.getInt(REBEL_FLEET_OFFSET).getText();
		try { gameState.setRebelFleetOffset(Integer.parseInt(newString)); }
		catch (NumberFormatException e) {}

		newString = sectorPanel.getInt(REBEL_FLEET_FUDGE).getText();
		try { gameState.setRebelFleetFudge(Integer.parseInt(newString)); }
		catch (NumberFormatException e) {}

		newString = sectorPanel.getInt(REBEL_PURSUIT_MOD).getText();
		try { gameState.setRebelPursuitMod(Integer.parseInt(newString)); }
		catch (NumberFormatException e) {}

		newString = sectorPanel.getInt(PLAYER_BEACON).getText();
		try { gameState.setCurrentBeaconId(Integer.parseInt(newString)); }
		catch (NumberFormatException e) {}

		gameState.setSectorHazardsVisible( sectorPanel.getBoolean(HAZARDS_VISIBLE).isSelected() );
	}
}
