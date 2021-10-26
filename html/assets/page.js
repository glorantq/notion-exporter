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

function toggleDarkMode() {
    setDarkMode(!isPageDarkMode());
    feather.replace();
    updatePrismStylesheet();
}

function updatePrismStylesheet() {
    let stylesheetLink = document.getElementById("prism-stylesheet");

    stylesheetLink.href = isPageDarkMode() ? prismStylesheets.dark : prismStylesheets.light;
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
if (isPageDarkMode()) {
    document.getElementsByTagName("html")[0].className = "dark-mode";
    themeToggle.innerHTML = `<i data-feather="sun"></i>`;
} else {
    document.getElementsByTagName("html")[0].className = "";
    themeToggle.innerHTML = `<i data-feather="moon"></i>`;
}
themeToggle.addEventListener("click", toggleDarkMode, false);
feather.replace();
updatePrismStylesheet();

let imagePopupContainer = document.getElementById("image-popup-container");
let imagePopup = document.getElementById("image-popup");
let imagePopupClose = document.getElementById("image-popup-close");

let imageElements = document.getElementsByClassName("image-block");
for (let imageElement of imageElements) {
    imageElement.addEventListener("click", (event) => {
        let clickedElement = event.currentTarget;
        imagePopup.src = clickedElement.src;

        imagePopupContainer.className = "image-popup-visible";
    }, false);
}

imagePopupClose.addEventListener("click", () => {
    imagePopupContainer.className = "image-popup-invisible";
}, false);

imagePopup.addEventListener("click", (event) => {
    event.stopPropagation();
}, false);

imagePopupContainer.addEventListener("click", () => {
    imagePopupContainer.className = "image-popup-invisible";
}, false);

let shareButton = document.getElementById("share-button");
let shareModal = document.getElementById("share-modal-container");
let shareUrl = document.getElementById("share-modal-url");

shareModal.addEventListener("click", (event) => {
    let isShareOpened = shareModal.className === "share-modal-visible";
    if (isShareOpened) {
        event.stopPropagation();
    }
}, false);

document.body.addEventListener("click", () => {
    let isShareOpened = shareModal.className === "share-modal-visible";
    if (isShareOpened) {
        shareModal.className = "share-modal-invisible";
    }
}, false);

shareButton.addEventListener("click", (event) => {
    event.stopPropagation();

    if(navigator.share && navigator.canShare) {
        let shareData = {
            url: window.location.href,
            title: window.document.title
        };

        if(navigator.canShare(shareData)) {
            let shareResult = navigator.share(shareData);
            shareResult.catch((error) => {
                if(!(error instanceof AbortError)) {
                    handleShareModal();
                } else {
                    console.log(error);
                }
            });
        }

        return;
    }

    handleShareModal();
}, false);

function handleShareModal() {
    let isOpened = shareModal.className === "share-modal-visible";

    if (isOpened) {
        shareModal.className = "share-modal-invisible";
    } else {
        shareUrl.value = document.location;
        document.getElementById("share-email").href = `mailto:?body=${encodeURIComponent(document.location)}`;
        document.getElementById("share-facebook").href = `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(document.location)}`;
        document.getElementById("share-twitter").href = `https://twitter.com/intent/tweet?url=${encodeURIComponent(document.location)}`;
        document.getElementById("share-reddit").href = `https://www.reddit.com/submit?url=${encodeURIComponent(document.location)}`;

        shareModal.className = "share-modal-visible";
    }
}

let shareCopyButton = document.getElementById("share-modal-copy");
shareCopyButton.addEventListener("click", () => {
    shareUrl.select();
    shareUrl.setSelectionRange(0, 99999999);

    if(navigator.clipboard) {
        navigator.clipboard.writeText(shareUrl.value);
    } else {
        document.execCommand("copy");
    }
}, false);

let loaderElements = document.body.getElementsByClassName("loader-container");
for (let loader of loaderElements) {
    loader.classList.toggle("loader-hidden");
}
