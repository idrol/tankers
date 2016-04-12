package net.tankers.main;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;

public class MainMenu extends GameState{

	@Override
	public void update(float delta) {
		isRunning = !nifty.update();
		
	}

	@Override
	public void render() {
		nifty.render(false);
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() {
		nifty.loadStyleFile("nifty-default-styles.xml");
		nifty.loadControlFile("nifty-default-controls.xml");
		createIntroScreen(nifty, new MyScreenController());
		nifty.gotoScreen("start");
	}
	
	public static class MyScreenController extends DefaultScreenController {
		@NiftyEventSubscriber(id="exit")
	    public void exit(final String id, final ButtonClickedEvent event) {
			nifty.exit();
	    }
	}
	
	private static Screen createIntroScreen(final Nifty nifty, final ScreenController controller) {
	    return new ScreenBuilder("start") {{
	      controller(controller);
	      layer(new LayerBuilder("layer") {{
	        childLayoutCenter();
	        onStartScreenEffect(new EffectBuilder("fade") {{
	          length(500);
	          effectParameter("start", "#0");
	          effectParameter("end", "#f");
	        }});
	        onEndScreenEffect(new EffectBuilder("fade") {{
	          length(500);
	          effectParameter("start", "#f");
	          effectParameter("end", "#0");
	        }});
	        onActiveEffect(new EffectBuilder("gradient") {{
	          effectValue("offset", "0%", "color", "#333f");
	          effectValue("offset", "100%", "color", "#ffff");
	        }});
	        panel(new PanelBuilder() {{
	          childLayoutVertical();
	          text(new TextBuilder() {{
	            text("Nifty 1.4 Core Hello World");
	            style("base-font");
	            color(Color.BLACK);
	            alignCenter();
	            valignCenter();
	          }});
	          panel(new PanelBuilder(){{
	            height(SizeValue.px(10));
	          }});
	          control(new ButtonBuilder("exit", "Pretty Cool!") {{
	            alignCenter();
	            valignCenter();
	          }});
	        }});
	      }});
	    }}.build(nifty);
	  }

}
