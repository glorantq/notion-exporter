<div id="top"></div>

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![GPLv3 License][license-shield]][license-url]


# Notion Exporter
A tool written in Java that exports your Notion.so workspace to a static website,

## About The Project
I started this project because I wanted a way to host my notes on my own server instead of Notion's, but every existing solution needed a Node.js backend, so I wansn't able to use my university's server.
This tool generates static HTML from Notion pages, with a CSS included that closely matches the default Notion look. JS code is included for dark mode switching, page sharing, and various other features related
to blocks. 

KaTeX is used for rendering math equations, code blocks are handled by Prism.js, and icons are provided by Feather. Images are automatically optimised for lower storage usage and faster page loads. Depending on page content
of course, you can achieve pretty good PageSpeed scores:

![PageSpeed Insights](git-assets/pagespeed.png "PageSpeed Insights Mobile")

## Usage
Bonyolult

## Building

### Prerequisites

To build the project on your machine, you will need:
* Java 8 (or newer)
* [Lombok](https://projectlombok.org/) plugin for your IDE

### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/glorantq/notion-exporter.git
   ```
2. Build the project with Gradle, or import into your favourite IDE

For testing purposes, two scripts are included for hosting your testing output with ngrok.io
* `run-tunnel.sh`
* `run-tunnel-wsl.bat` (this version requires WSL)

These scripts will automatically open an ngrok tunnel to the directory of your choice (`test-out` by default), and open your default browser (requires `wslview` to be installed)

[contributors-shield]: https://img.shields.io/github/contributors/glorantq/notion-exporter.svg?style=flat
[contributors-url]: https://github.com/glorantq/notion-exporter/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/glorantq/notion-exporter.svg?style=flat
[forks-url]: https://github.com/glorantq/notion-exporter/network/members
[stars-shield]: https://img.shields.io/github/stars/glorantq/notion-exporter.svg?style=flat
[stars-url]: https://github.com/glorantq/notion-exporter/stargazers
[license-shield]: https://img.shields.io/github/license/glorantq/notion-exporter.svg?style=flat
[license-url]: https://github.com/glorantq/notion-exporter/blob/master/LICENSE.md
