# MinesweeperFX
MinesweeperFX is an implementation of the classic Minesweeper game with a twist. It was created as part of the Multimedia Technology class of the [School of Electrical and Computer Engineering](https://www.ece.ntua.gr/en) of the [National Technical University of Athens](https://ntua.gr/en/) during the academic year 2022-2023.

#### Contributors
`gkapetanakis` - Georgios Kapetanakis (me)

#### Grade
The project was graded with a 10 out of 10.

## Jump to a Section
* [Repository Contents](#repository-contents)
* [How is MinesweeperFX different from Minesweeper?](#how-is-minesweeperfx-different-from-minesweeper)
* [How does MinesweeperFX work?](#how-does-minesweeperfx-work)
* [Setup Guide](#setup-guide)
* [Usage Guide](#usage-guide)

## Repository Contents
* `handouts`: A folder containing the assignment description in ~~both English and Greek~~ Greek (for now).
* `src`: A folder containing the project's source code, written in Java using Gluon's JavaFX library and Scene Builder tool.

## How is MinesweeperFX different from Minesweeper?
MinesweeperFX allows the user to create configuration files (called 'scenarios') to explicitly set various parameters of the game. These parameters are:
* the available game time,
* the number of mines,
* whether a supermine exists or not.

A supermine acts like a regular mine with the addition that marking it with a flag before performing 4 successful left clicks, reveals all tiles on its row and column (disabling any revealed mines).

## How does MinesweeperFX work?
* The main class (i.e., with the main method) of the application is the `App` class.
* There is a `Settings` class which contains parameters for the entire application. Each parameter has a comment explaining its function.
* Graphics are implemented using an image that functions as a tileset. It is loaded as an `Image`, and pieces of it are used independently as `ImagePatterns` which fill `Rectangle` objects. The tileset is present in the `src/resources` folder, along with the fontface used by the application.
* The Model-View-Controller design pattern is used in several classes as follows: for each class `T`, which functions as a Model, an FXML file named `TView.fxml` functions as its View, and a class `TController` functions as its Controller. The View and the Controller are not visible outside of the packages in which they are located. The `Mine` and `Cell` classes are very simple, so they do not require separate View and Controller.
* Javadoc comments have been added for the Minesweeper class.

## Setup Guide
Will be updated soon.

## Usage Guide
Will be updated soon.
