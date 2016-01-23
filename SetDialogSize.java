
    /**
     * This code sets the size of a dialog to 90% of the screen width
     */
    @Override
    public void show() {
	// Force dialog to 90% of the screen width

	// retrieve display dimensions
	Rect rect = new Rect();
	Window window = ctx.getWindow();
	window.getDecorView().getWindowVisibleDisplayFrame(rect);

	WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	//lp.copyFrom(getWindow().getAttributes());
	lp.width = (int)(rect.width() * 0.9f);
	//lp.height = (int)(rect.height() * 0.9f);

	super.show();
	getWindow().setAttributes(lp);
    }
