import { useState, useEffect, useRef, useContext } from 'react';
import styles from "./Header.module.css";
import image from "../../images/user.jpg";
import { useNavigate } from 'react-router-dom';
import { PopUp } from "./PopUp/PopUp";
import { ChatsContext } from './../../Context/ChatsContext';

export const Header = () => {

    /* PopUp 관련 */
    const [showPopUp, setShowPopUp] = useState(false);
    const menuRef = useRef(null);
    const popupRef = useRef(null);

    const togglePopUp = () => {
        setShowPopUp(prevState => !prevState); // 상태를 토글
    };
    // 다른 곳 click시 popUp창 hide
    const handleClickOutside = (event) => {
        if (
            menuRef.current &&
            !menuRef.current.contains(event.target) &&
            popupRef.current &&
            !popupRef.current.contains(event.target)
        ) {
            setShowPopUp(false);
        }
    };

    useEffect(() => {
        document.addEventListener("mousedown", handleClickOutside);
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, []);

    /* Font Awesome */
    useEffect(() => {
        // 스타일시트 추가
        const faLink = document.createElement("link");
        faLink.rel = "stylesheet";
        faLink.href =
            "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css";
        document.head.appendChild(faLink);

        // 컴포넌트가 언마운트될 때 스타일시트 제거
        return () => {
            document.head.removeChild(faLink);
        };
    }, []);

    const navi = useNavigate();

    const {setChatNavi} =useContext(ChatsContext);
    const handleChat=()=>{
        setChatNavi("home");
    }

    return (
        <div className={styles.container}>
            <div className={`${styles.left} ${showPopUp ? styles.dropdownActive : ''}`}>
                <div className={styles.logo} onClick={() => { navi("/") }}>NBTI</div>
                <div className={styles.menu_dropdown} onClick={togglePopUp} ref={menuRef}>
                    <p>오피스 홈</p>
                    <i className="fa-solid fa-caret-down"></i>
                </div>
            </div>
            <div className={styles.right}>
                <div className={styles.chat}>
                    <i className="fa-regular fa-comments fa-xl" onClick={handleChat}></i>
                </div>
                <div className={styles.alarm}>
                    <i className="fa-regular fa-bell fa-xl"></i>
                </div>
                <div className={styles.user_info}>
                    <div className={styles.user_profile_img}>
                        <img src={image} alt="" />
                    </div>
                </div>
            </div>
            {showPopUp && <PopUp ref={popupRef} onClose={() => setShowPopUp(false)} />} {/* 조건부 렌더링 */}
        </div>
    );
};