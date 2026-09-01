package com.example.madgwicktest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.madgwicktest.ui.screens.AlgorithmMenuScreen
import com.example.madgwicktest.ui.screens.ComparisonScreen
import com.example.madgwicktest.ui.screens.GameSetupScreen
import com.example.madgwicktest.ui.screens.GraphComparisonScreen
import com.example.madgwicktest.ui.screens.HomeScreen
import com.example.madgwicktest.ui.screens.ResultScreen
import com.example.madgwicktest.ui.screens.SingleMethodSetupScreen
import com.example.madgwicktest.ui.screens.SingleMethodScreen
import com.example.madgwicktest.ui.theme.MadgwickTestTheme
import com.example.madgwicktest.ui.viewmodel.GameViewModel
import com.example.madgwicktest.ui.viewmodel.MeasurementScreenVM

class MainActivity : FragmentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            MadgwickTestTheme {

                val navController =
                    rememberNavController()

                val measurementVM:
                        MeasurementScreenVM =
                    viewModel()

                val gameVM:
                        GameViewModel =
                    viewModel()

                val gameLauncher =
                    rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult()
                    ) { result ->
                        if (result.resultCode == Activity.RESULT_OK) {
                            val score =
                                result.data?.getIntExtra(
                                    GodotGameActivity.EXTRA_SCORE,
                                    0
                                ) ?: 0

                            val finalError =
                                result.data?.getFloatExtra(
                                    GodotGameActivity.EXTRA_FINAL_ERROR,
                                    0f
                                ) ?: 0f

                            gameVM.setExternalResult(
                                score = score,
                                finalError = finalError
                            )

                            navController.navigate(
                                "result"
                            )
                        }
                    }

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {

                    composable("home") {

                        HomeScreen(
                            onGameClicked = {
                                navController.navigate(
                                    "game_setup"
                                )
                            },

                            onTestAlgorithmsClicked = {
                                navController.navigate(
                                    "algorithm_menu"
                                )
                            }
                        )
                    }

                    composable("algorithm_menu") {

                        AlgorithmMenuScreen(
                            onSingleMethodClicked = {
                                navController.navigate(
                                    "single_method_setup"
                                )
                            },

                            onBallComparisonClicked = {
                                navController.navigate(
                                    "comparison"
                                )
                            },

                            onGraphComparisonClicked = {
                                navController.navigate(
                                    "graph_comparison"
                                )
                            }
                        )
                    }

                    composable("single_method_setup") {

                        SingleMethodSetupScreen(
                            onStartClicked = { method ->
                                measurementVM.setMethod(
                                    method
                                )

                                navController.navigate(
                                    "measurement"
                                )
                            }
                        )
                    }

                    composable("game_setup") {

                        GameSetupScreen(
                            gameViewModel = gameVM,
                            onStartClicked = { method, durationSeconds ->

                                measurementVM.setMethod(
                                    method
                                )

                                gameVM.configure(
                                    method = method,
                                    durationSeconds = durationSeconds
                                )

                                gameLauncher.launch(
                                    Intent(
                                        this@MainActivity,
                                        GodotGameActivity::class.java
                                    ).apply {
                                        putExtra(
                                            GodotGameActivity.EXTRA_METHOD,
                                            method.name
                                        )
                                        putExtra(
                                            GodotGameActivity.EXTRA_DURATION_SECONDS,
                                            durationSeconds
                                        )
                                    }
                                )
                            }
                        )
                    }

                    composable("result") {

                        ResultScreen(
                            gameViewModel = gameVM,
                            onTryAgainClicked = {
                                navController.navigate(
                                    "game_setup"
                                )
                            },
                            onHomeClicked = {
                                navController.navigate(
                                    "home"
                                )
                            }
                        )
                    }

                    composable("measurement") {

                        SingleMethodScreen(
                            viewModel = measurementVM
                        )
                    }

                    composable("comparison") {

                        ComparisonScreen(
                            vm = measurementVM
                        )
                    }

                    composable("graph_comparison") {

                        GraphComparisonScreen(
                            vm = measurementVM
                        )
                    }
                }
            }
        }
    }
}
