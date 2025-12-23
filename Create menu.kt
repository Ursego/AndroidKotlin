// @ If res/menu directory doesn't exist: right click "res" directory > New > Directory > enter the directory name "menu" > OK.

// @ Right click "res/menu" directory > New > File > enter the menu name "XXX_activity_menu.xml" (like "main_activity_menu.xml" if you are creating the menu for MainActivity) > OK > XML > OK > The menu designer opened.

// @ Make its text this (the example creates a menu with one item; that item opens the Settings screen, described here; of course, you can customize according to your needs, and add more items):

<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:android="http://schemas.android.com/apk/res/android">

    <item
        android:id="@+id/openPrefActivity"
        android:icon="@android:drawable/ic_menu_manage"
        android:title="@string/word__settings"
        app:showAsAction="never" />
</menu>

// @ If you want to change the icon (or add icons to other menu items): switch the menu XML from Text to Design view > in Component Tree, click the menu item > Attributes > icon > click on the vertical line ("Pick a resource").

// @ Add to the Activity, for which the menu is created:

    /***********************************************************************************************************************/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        (menu as MenuBuilder).setOptionalIconsVisible(true) // remove this line if your menu items don't have icons
        menuInflater.inflate(R.menu.main_activity_menu, menu) // change main_activity_menu to the actual name of your menu if it's different
        return super.onCreateOptionsMenu(menu)
    }
    /***********************************************************************************************************************/
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.<your menu item id> -> {
                val intent = Intent(this, PrefActivity::class.java) // change PrefActivity to the actual name of the activity the item opens
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item!!)
    }
    /***********************************************************************************************************************/

// SET MENU ITEM VISIBILITY PROGRAMMATICALLY

// If you want to make a menu item visible/invisible depending on a runtime condition, do the following:

// @ In the Activity, which displays the menu, override the function onPrepareOptionsMenu(). In the following example, I show the menu item "Delete Elephant" only if the elephant exists:

override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
    menu!!.findItem(R.id.delete_elephant)!!.isVisible = elephantExists()
    return super.onPrepareOptionsMenu(menu)
}