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