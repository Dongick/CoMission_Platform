import { createGlobalStyle } from "styled-components";
import NotoSansKRRegular from "../assets/fonts/NotoSans-Regular.woff2";
import NotoSansKRSemiBold from "../assets/fonts/NotoSans-Bold.woff2";
import UbuntuBold from "../assets/fonts/Ubuntu-Bold.woff";
import UbuntuRegular from "../assets/fonts/Ubuntu-Regular.woff";
import Gmarket1 from "../assets/fonts/GmarketSansTTFLight.woff";
import Gmarket2 from "../assets/fonts/GmarketSansTTFMedium.woff";
import Gmarket3 from "../assets/fonts/GmarketSansTTFBold.woff";

const GlobalFont = createGlobalStyle`
    @font-face {
        font-family: "noto";
        src: local("noto"), url(${NotoSansKRRegular}) format('woff2');
        font-weight: normal;
        font-display: swap;
    }
    @font-face {
        font-family: "notoBold";
        src: local("notoBold"), url(${NotoSansKRSemiBold}) format('woff2');
        font-weight: bold;
        font-display: swap;
    }
    @font-face {
        font-family: "ubuntu";
        src: local("ubuntu"), url(${UbuntuRegular}) format('woff');
        font-weight: normal;
        font-display: swap;
    }

    @font-face {
        font-family: "ubuntuBold";
        src: local("ubuntuBold"), url(${UbuntuBold}) format('woff');
        font-weight: bold;
        font-display: swap;
    }
    @font-face {
        font-family: "gmarket1";
        src: local("gmarket1"), url(${Gmarket1}) format('woff');
        font-weight: 300;
        font-display: swap;
    }
    @font-face {
        font-family: "gmarket2";
        src: local("gmarket2"), url(${Gmarket2}) format('woff');
        font-weight: 500;
        font-display: swap;
    }
    @font-face {
        font-family: "gmarket3";
        src: local("gmarket3"), url(${Gmarket3}) format('woff');
        font-weight: 700;
        font-display: swap;
    }
`;

export default GlobalFont;
