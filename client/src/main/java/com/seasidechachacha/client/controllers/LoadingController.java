package com.seasidechachacha.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

public class LoadingController
{
    @FXML
    ProgressBar pbSplash;
    @FXML
    ProgressIndicator piSplash;


    public ProgressBar getPBSplashValue()
    {
        return pbSplash;
    }

    public ProgressIndicator getPISplash()
    {
        return piSplash;
    }

}