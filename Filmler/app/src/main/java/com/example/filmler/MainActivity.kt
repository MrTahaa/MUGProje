package com.example.filmler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.filmler.data.local.AppDatabase
import com.example.filmler.ui.AddFilmScreen
import com.example.filmler.ui.DetailScreen
import com.example.filmler.data.repository.FilmRepository
import com.example.filmler.ui.FilmViewModel
import com.example.filmler.ui.FilmViewModelFactory
import com.example.filmler.ui.HomeScreen
import com.example.filmler.ui.SearchPreferences
import com.example.filmler.ui.theme.FilmlerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val repository = FilmRepository(database.filmDao())
        val searchPrefs = SearchPreferences(this)
        val viewModel: FilmViewModel by viewModels {
            FilmViewModelFactory(repository, searchPrefs)
        }

        setContent {
            FilmlerTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "home") {

                        // ANA EKRAN
                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onFilmClick = { filmId -> navController.navigate("detail/$filmId") },
                                onAddFilmClick = { navController.navigate("add_film") }
                            )
                        }

                        // DETAY EKRANI
                        composable("detail/{filmId}") { backStackEntry ->
                            val filmId = backStackEntry.arguments?.getString("filmId")?.toIntOrNull() ?: 0
                            DetailScreen(viewModel = viewModel, filmId = filmId, onBackClick = { navController.popBackStack() })
                        }
                        //FİLM EKLEME EKRANI
                        composable("add_film") {
                            AddFilmScreen(
                                viewModel = viewModel,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}