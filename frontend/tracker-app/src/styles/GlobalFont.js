import reset from "styled-reset";
import { createGlobalStyle } from "styled-components";
import NotoSansKRRegular from "../assets/fonts/NotoSansKR-Regular.woff";
import NotoSansKRSemiBold from "../assets/fonts/NotoSansKR-SemiBold.woff";
import UbuntuBold from "../assets/fonts/Ubuntu-Bold.woff";
import UbuntuRegular from "../assets/fonts/Ubuntu-Regular.woff";
const GlobalFont = createGlobalStyle`
    @font-face {
        font-family: "noto";
        src: local("noto"), url(${NotoSansKRRegular}) format('woff'); 
        font-weight: normal;
    }
    @font-face {
        font-family: "notoBold";
        src: local("notoBold"), url(${NotoSansKRSemiBold}) format('woff'); 
        font-weight: bold;
    }
    @font-face {
        font-family: "ubuntu";
        src: local("ubuntu"), url(${UbuntuRegular}) format('woff');
        font-weight: normal;
    }

    @font-face {
        font-family: "ubuntuBold";
        src: local("ubuntuBold"), url(${UbuntuBold}) format('woff');
        font-weight: bold;
    }
  }

`;

export default GlobalFont;
