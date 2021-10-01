function toggleDarkMode() {
    let themeToggle = document.getElementById("theme-toggle");

    let isDarkMode = window.localStorage.getItem("dark-mode") === "true";
    if (isDarkMode === undefined) {
        isDarkMode = false;
    }

    isDarkMode = !isDarkMode;

    if (isDarkMode) {
        document.getElementsByTagName("html")[0].className = "dark-mode";
        themeToggle.innerHTML = `<i data-feather="sun"></i>`;
    } else {
        document.getElementsByTagName("html")[0].className = "";
        themeToggle.innerHTML = `<i data-feather="moon"></i>`;
    }

    window.localStorage.setItem("dark-mode", isDarkMode);
    feather.replace();
}

twemoji.parse(document.body);

let mathElements = document.body.getElementsByClassName("katex-math");
for (let math of mathElements) {
    katex.render(math.getAttribute("data-equation"), math, {
        throwOnError: false
    });
}

let liCounter = 0;
let allElements = document.body.getElementsByClassName("block");
for (let element of allElements) {
    let type = element.getAttribute("data-type");
    if (type === "NotionNumberedListItemBlock") {
        element.getElementsByClassName("numbered-list-marker")[0].setAttribute("data-before", `${++liCounter}.`);
    } else {
        liCounter = 0;
    }
}

let themeToggle = document.getElementById("theme-toggle");
let isDarkMode = window.localStorage.getItem("dark-mode") === "true";
if (isDarkMode === undefined) {
    isDarkMode = false;
}

if (isDarkMode) {
    document.getElementsByTagName("html")[0].className = "dark-mode";
    themeToggle.innerHTML = `<i data-feather="sun"></i>`;
} else {
    document.getElementsByTagName("html")[0].className = "";
    themeToggle.innerHTML = `<i data-feather="moon"></i>`;
}

window.localStorage.setItem("dark-mode", isDarkMode);
themeToggle.addEventListener("click", toggleDarkMode);
feather.replace();

let loaderElements = document.body.getElementsByClassName("loader-container");
for (let loader of loaderElements) {
    loader.classList.toggle("loader-hidden");
}