package me.fril.regeneration.debugger;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import me.fril.regeneration.RegenerationMod;
import me.fril.regeneration.common.capability.IRegeneration;

public class RegenDebugger {
	
	private static final JFrame frame;
	private static final JTabbedPane tabs;
	private static final Map<IRegeneration, DebugChannelTab> tabReg = new HashMap<>();
	
	static {
		frame = new JFrame("Regeneration v"+RegenerationMod.VERSION+" DEBUGGER");
		frame.setAutoRequestFocus(false);
		frame.setSize(500, 560);
		
		tabs = new JTabbedPane();
		frame.add(tabs, BorderLayout.CENTER);
		
		String optX = System.getProperty("debuggerX"),
				optY = System.getProperty("debuggerY");
		int dx = optX == null ? 0 : Integer.valueOf(optX),
				dy = optY == null ? 0 : Integer.valueOf(optY);
		frame.setLocationRelativeTo(null);
		frame.setLocation(frame.getX()+dx, frame.getY()+dy);
		
		frame.setVisible(true);
	}
	
	private RegenDebugger() {}
	
	
	public static IDebugChannel registerPlayer(IRegeneration capability) {
		if (tabReg.containsKey(capability))
			return tabReg.get(capability).getChannel();
		
		DebugChannelTab tab = new DebugChannelTab(capability);
		EventQueue.invokeLater(()->tabs.addTab(tab.getName(), tab));
		tabReg.put(capability, tab);
		return tab.getChannel();
	}
	
	
	public static void unregisterPlayer(IRegeneration capability) {
		EventQueue.invokeLater(()->{
			//TODO mark "finished" session
		});
		tabReg.remove(capability);
	}
	
	
	public static void open() {
		frame.setVisible(true);
	}
	
}