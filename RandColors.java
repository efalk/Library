
package org.efalk.utils

import java.util.Random;
import android.view.View;
import android.view.ViewGroup;

/**
 * Utility:  Assign random light colors to the backgrounds of all the
 * widgets in the given hierarchy.  Used to debug layouts.
 */
class RandColors {
    static Random rng = null;
    static public void Colors(View top, int base) {
      if( top == null ) return;
      if( rng == null ) rng = new Random();
      int color = 0xff000000 | base |
	(rng.nextInt(128) << 16) |
	(rng.nextInt(128) << 8) |
	rng.nextInt(128);
      top.setBackgroundColor(color);
      if( top instanceof ViewGroup ) {
	ViewGroup parent = (ViewGroup) top;
	int n = parent.getChildCount();
	for( int i = 0; i < n; ++i ) {
	  Colors(parent.getChildAt(i), base);
	}
      }
    }
    static public void Colors(View top) {
      Colors(top, 0x808080);
    }
    static public void ColorsDark(View top) {
      Colors(top, 0);
    }
}
