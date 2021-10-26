/*
 *      Copyright (C) 2021 Gerber Lóránt Viktor
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

function isSystemDarkMode() {
    return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
}

function isPageDarkMode() {
    let isDarkMode = window.localStorage.getItem("dark-mode") === "true";
    if (isDarkMode === undefined) {
        isDarkMode = isSystemDarkMode();
    }

    return isDarkMode;
}

function setDarkMode(isDarkMode) {
    let themeToggle = document.getElementById("theme-toggle");

    if (isDarkMode) {
        document.getElementsByTagName("html")[0].className = "dark-mode";

        if(themeToggle) {
            themeToggle.innerHTML = `<i data-feather="sun"></i>`;
        }
    } else {
        document.getElementsByTagName("html")[0].className = "";

        if(themeToggle) {
            themeToggle.innerHTML = `<i data-feather="moon"></i>`;
        }
    }

    window.localStorage.setItem("dark-mode", isDarkMode);
}

setDarkMode(isPageDarkMode());