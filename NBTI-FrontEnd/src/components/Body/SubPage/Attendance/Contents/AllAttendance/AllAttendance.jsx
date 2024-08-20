import React, { useState, useCallback, useEffect, useRef } from 'react';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import koLocale from '@fullcalendar/core/locales/ko';
import useAllWeeklyStats from './useAllAttendance';
import styles from './AllAttendance.module.css';
import SearchUser from './SearchUser/SearchUser';

// 시간 포맷 함수
const formatTime = (date) => {
    const options = { hour: '2-digit', minute: '2-digit' };
    return new Date(date).toLocaleTimeString('ko-KR', options);
};

// 날짜 포맷 함수
const formatDate = (date) => new Date(date).toISOString().split('T')[0];

// 요일 계산 함수 (월요일을 주의 시작일로 설정)
const getDayOfWeek = (date) => (date.getDay() + 6) % 7; // 0 (월요일)부터 6 (일요일)

// 주의 시작일 계산 함수 (월요일)
const getStartOfWeek = (date) => {
    const day = date.getDay();
    const distanceToMonday = (day + 6) % 7;
    const monday = new Date(date);
    monday.setDate(date.getDate() - distanceToMonday);
    return monday;
};

// 근무 시간 계산 함수
const calculateWorkingHours = (startDate, endDate) => {
    if (!startDate || !endDate) return 'N/A';

    const start = new Date(startDate);
    const end = new Date(endDate);

    const diffMs = end - start;
    if (diffMs < 0) return 'N/A'; // 유효하지 않은 시간

    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
    const diffMinutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));

    return `${diffHours}시간 ${diffMinutes}분`;
};

// 기본 이벤트 생성 함수
const createDefaultEvent = (date, memberName, teamName) => {
    return {
        title: ' 출근: N/A\n퇴근: N/A\n근무 시간: N/A\n',
        start: date,
        extendedProps: {
            backgroundColor: 'gray',
            textColor: 'white',
            dayOfWeek: getDayOfWeek(new Date(date)),
            memberName,
            teamName
        }
    };
};

export const AllAttendance = () => {
    const { stats, loading } = useAllWeeklyStats();
    const [searchTerm, setSearchTerm] = useState('');
    const [filteredMembers, setFilteredMembers] = useState([]);
    const [list, setList] = useState([]);
    const [currentWeekStart, setCurrentWeekStart] = useState(getStartOfWeek(new Date()));
    const calendarRef = useRef(null);

    const handleList = useCallback(() => {
        const startOfWeek = getStartOfWeek(currentWeekStart);
        const endOfWeek = new Date(startOfWeek);
        endOfWeek.setDate(startOfWeek.getDate() + 6); // 주의 끝 날짜

        const membersToDisplay = filteredMembers.length > 0 ? filteredMembers : Object.keys(stats).map(memberId => {
            const memberStats = stats[memberId] || {};
            const hasAttendance = Object.keys(memberStats).length > 0;
            return {
                memberId,
                name: hasAttendance ? memberStats[Object.keys(memberStats)[0]].name : 'N/A',
                teamName: hasAttendance ? memberStats[Object.keys(memberStats)[0]].team_name : 'N/A'
            };
        });

        const events = membersToDisplay.flatMap(({ memberId, name, teamName }) => {
            return Array.from({ length: 7 }).map((_, index) => {
                const date = new Date(startOfWeek);
                date.setDate(date.getDate() + index);
                const formattedDate = formatDate(date);

                const { startDate, endDate } = stats[memberId]?.[formattedDate] || {};

                const startTime = startDate ? formatTime(startDate) : 'N/A';
                const endTime = endDate ? formatTime(endDate) : 'N/A';
                const workingHours = calculateWorkingHours(startDate, endDate);

                const title = `출근: ${startTime}\n퇴근: ${endTime}\n근무 시간: ${workingHours}\n`;

                let backgroundColor = 'white';
                let textColor = 'black';

                if (!startDate && !endDate) {
                    backgroundColor = 'gray';
                    textColor = 'white';
                }

                return {
                    title,
                    start: formattedDate,
                    extendedProps: {
                        backgroundColor,
                        textColor,
                        dayOfWeek: getDayOfWeek(date),
                        memberName: name,
                        teamName
                    }
                };
            });
        });
        setList(events);
    }, [filteredMembers, stats, currentWeekStart]);

    useEffect(() => {
        handleList();
    }, [handleList]);

    const handleSearch = useCallback(() => {
        if (searchTerm.trim() === '') {
            const allMembers = Object.keys(stats).map(memberId => {
                const memberStats = stats[memberId] || {};
                const hasAttendance = Object.keys(memberStats).length > 0;
                return {
                    memberId,
                    name: hasAttendance ? memberStats[Object.keys(memberStats)[0]].name : 'N/A',
                    teamName: hasAttendance ? memberStats[Object.keys(memberStats)[0]].team_name : 'N/A'
                };
            });
            setFilteredMembers(allMembers);
        } else {
            const lowerCaseSearchTerm = searchTerm.toLowerCase();
            const filtered = Object.keys(stats).map(memberId => {
                const memberStats = stats[memberId] || {};
                const hasAttendance = Object.keys(memberStats).length > 0;
                const name = hasAttendance ? memberStats[Object.keys(memberStats)[0]].name : 'N/A';
                const teamName = hasAttendance ? memberStats[Object.keys(memberStats)[0]].team_name : 'N/A';
                return { memberId, name, teamName };
            }).filter(({ name }) => name.toLowerCase().includes(lowerCaseSearchTerm));

            setFilteredMembers(filtered);
        }
    }, [searchTerm, stats]);

    useEffect(() => {
        handleSearch();
    }, [searchTerm, stats, handleSearch]);

    const handleDatesSet = (info) => {
        const startOfWeek = getStartOfWeek(info.view.currentStart);
        if (startOfWeek.getTime() !== currentWeekStart.getTime()) {
            setCurrentWeekStart(startOfWeek);
        }
    };

    const handlePrevWeek = () => {
        setCurrentWeekStart(prev => {
            const startOfWeek = new Date(prev);
            startOfWeek.setDate(startOfWeek.getDate() - 7);
            return getStartOfWeek(startOfWeek);
        });
    };

    const handleNextWeek = () => {
        setCurrentWeekStart(prev => {
            const startOfWeek = new Date(prev);
            startOfWeek.setDate(startOfWeek.getDate() + 7);
            return getStartOfWeek(startOfWeek);
        });
    };

    const handleTodayClick = () => {
        const today = new Date();
        setCurrentWeekStart(getStartOfWeek(today));
    };

    if (loading) {
        return <div>Loading...</div>;
    }

    return (
        <div className={styles.container}>
            <SearchUser
                searchTerm={searchTerm}
                setSearchTerm={setSearchTerm}
                onSearch={handleSearch}
            />
            <FullCalendar
                ref={calendarRef}
                plugins={[dayGridPlugin, interactionPlugin]}
                initialView="dayGridWeek"
                locales={[koLocale]}
                locale="ko"
                selectable={true}
                height="auto"
                events={list}
                firstDay={1} // 월요일을 주의 시작일로 설정
                headerToolbar={{
                    left: 'prev,next today', // 왼쪽 버튼
                    center: 'title',         // 가운데 제목
                    right: ''                // 오른쪽 버튼을 빈 문자열로 설정하여 기본 툴바 버튼 제거
                }}
                datesSet={handleDatesSet}
                eventContent={({ event }) => {
                    const lines = event.title.split('\n');

                    return (
                        <div
                            className={styles.eventContent}
                            style={{ backgroundColor: event.extendedProps.backgroundColor, color: event.extendedProps.textColor }}
                        >
                            <div style={{ display: "flex", flexDirection: "column", alignItems: 'flex-start' }}>
                                <div>이름: {event.extendedProps.memberName}</div>
                                <div>부서: {event.extendedProps.teamName}</div>
                            </div>
                            <div style={{ display: "flex", flexDirection: "column", textAlign: "center", flex: 1 }}>
                                {lines.map((line, index) => (
                                    <div key={index}>{line}</div>
                                ))}
                            </div>
                        </div>
                    );
                }}
            />
            <div className={styles.buttonContainer}>
                <button onClick={handlePrevWeek} className={styles.navButton}>이전 주</button>
                <button onClick={handleTodayClick} className={styles.navButton}>오늘</button>
                <button onClick={handleNextWeek} className={styles.navButton}>다음 주</button>
            </div>
        </div>
    );
};
