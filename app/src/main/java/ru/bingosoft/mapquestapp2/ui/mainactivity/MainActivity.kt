package ru.bingosoft.mapquestapp2.ui.mainactivity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.navigation.NavigationView
import dagger.android.AndroidInjection
import ru.bingosoft.mapquestapp2.R
import ru.bingosoft.mapquestapp2.db.Checkup.Checkup
import ru.bingosoft.mapquestapp2.db.Orders.Orders
import ru.bingosoft.mapquestapp2.ui.checkup.CheckupFragment
import ru.bingosoft.mapquestapp2.ui.checkuplist.CheckupListFragment
import ru.bingosoft.mapquestapp2.ui.login.LoginActivity
import ru.bingosoft.mapquestapp2.util.Const
import ru.bingosoft.mapquestapp2.util.Toaster
import timber.log.Timber
import javax.inject.Inject


class MainActivity : AppCompatActivity(), FragmentsContractActivity,
    NavigationView.OnNavigationItemSelectedListener, MainActivityContractView {

    @Inject
    lateinit var mainPresenter: MainActivityPresenter
    @Inject
    lateinit var toaster: Toaster

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        /*val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_slideshow,
                R.id.nav_checkup
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setNavigationItemSelectedListener(this)
        //navView.setupWithNavController(navController) // Переключалка фрагментов по-умолчанию
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun setCheckup(checkup: Checkup) {
        Timber.d("setCheckup from Activity")
        val cf=this.supportFragmentManager.findFragmentByTag("checkup_fragment_tag") as? CheckupFragment
        cf?.dataIsLoaded(checkup)
    }

    override fun setChecupListOrder(order: Orders) {
        Timber.d("setChecupListOrder from Activity")
        val clf=this.supportFragmentManager.findFragmentByTag("checkup_list_fragment_tag") as? CheckupListFragment
        clf?.showCheckupListOrder(order)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)

        when (item.itemId) {
            R.id.nav_home -> {
                navController.navigate(R.id.nav_home)
                return true
            }
            R.id.nav_gallery -> {
                navController.navigate(R.id.nav_gallery)
                return true
            }
            R.id.nav_slideshow -> {
                navController.navigate(R.id.nav_slideshow)
                return true
            }
            R.id.nav_send_data -> {
                Timber.d("Отправляем данные на сервер")
                mainPresenter.attachView(this)
                mainPresenter.sendData()

                return true
            }
            R.id.nav_auth -> {
                // Запустим активити с настройками
                val intent = Intent(this, LoginActivity::class.java)
                startActivityForResult(intent, Const.RequestCodes.AUTH)
                return true
            }
            else -> {
                return false
            }

        }

    }

    override fun showMainActivityMsg(resID: Int) {
        toaster.showToast(resID)
    }

}