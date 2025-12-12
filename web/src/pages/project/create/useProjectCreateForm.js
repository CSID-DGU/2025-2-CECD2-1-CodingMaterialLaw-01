import { useState, useEffect } from 'react';
import { v4 as uuidv4 } from 'uuid';

export function useProjectCreateForm() {
  const [projectType, setProjectType] = useState('BOTH');
  const [title, setTitle] = useState('');
  const [participant, setParticipant] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [description, setDescription] = useState('');

  const [termsOfPolicy, setTermsOfPolicy] = useState('');
  const [privacyPolicy, setPrivacyPolicy] = useState('');
  const [healthDataConsent, setHealthDataConsent] = useState('');
  const [airDataConsent, setAirDataConsent] = useState('');
  const [localDataTermsOfService, setLocalDataTermsOfService] = useState('');

  const [personalInfo, setPersonalInfo] = useState({
    gender: false, birthDate: false, bloodType: false,
    height: false, weight: false, email: false, phoneNumber: false, name: false, nationalCode: false
  });

  const [healthData, setHealthData] = useState({
    steps: false,
    heartRate: false,
    runningSpeed: false,
    basalEnergyBurned: false,
    activeEnergyBurned: false,
    sleepAnalysis: false,
    oxygenSaturation: false,
    bloodPressureSystolic: false,
    bloodPressureDiastolic: false,
    respiratoryRate: false,
    bodyTemperature: false,
    ecgData: false,
    watchDeviceLatitude: false,
    watchDeviceLongitude: false
  });

  const [airData, setAirData] = useState({
    pm25Value: false,
    pm25Level: false,
    pm10Value: false,
    pm10Level: false,
    temperature: false,
    temperatureLevel: false,
    humidity: false,
    humidityLevel: false,
    co2Value: false,
    co2Level: false,
    vocValue: false,
    vocLevel: false,
    picoDeviceLatitude: false,
    picoDeviceLongitude: false
  });

  const [airMetaDataItems, setAirMetaDataItems] = useState([]);
  const [errors, setErrors] = useState({});

  const showHealth = projectType === 'HEALTH_DATA' || projectType === 'BOTH';
  const showAir = projectType === 'AIR_QUALITY' || projectType === 'BOTH';

  const addMetaData = () => {
    setAirMetaDataItems(prev => [...prev, { id: uuidv4(), dataName: '', dataType: 'INTEGER' }]);
  };

  const removeMetaData = (idToRemove) => {
    setAirMetaDataItems(prev => prev.filter(item => item.id !== idToRemove));
    setErrors(prev => {
      const newErrors = { ...prev };
      delete newErrors[`airMetaDataItems.${idToRemove}.dataName`];
      return newErrors;
    });
  };

  const updateMetaDataItem = (index, field, value) => {
    const updated = [...airMetaDataItems];
    updated[index][field] = value;
    setAirMetaDataItems(updated);
  };

  const validateForm = () => {
    const newErrors = {};
    if (!title) newErrors.title = '제목을 입력하세요';
    if (!participant) newErrors.participant = '참여 인원을 입력하세요';
    if (!startDate) newErrors.startDate = '시작 날짜를 선택하세요';
    if (!endDate) newErrors.endDate = '종료 날짜를 선택하세요';
    if (!description) newErrors.description = '설명을 입력하세요';
    if (!termsOfPolicy) newErrors.termsOfPolicy = '이용약관을 입력하세요';
    if (!privacyPolicy) newErrors.privacyPolicy = '개인정보 처리방침을 입력하세요';
    if (!localDataTermsOfService) newErrors.localDataTermsOfService = '위치 정보 약관을 입력하세요';
    if (showHealth && !healthDataConsent) newErrors.healthDataConsent = '헬스 데이터 동의를 입력하세요';
    if (showAir && !airDataConsent) newErrors.airDataConsent = '공기 데이터 동의를 입력하세요';

    if (showHealth && !Object.values(healthData).some(Boolean)) {
      newErrors.healthData = '헬스 데이터 항목을 최소 1개 이상 선택하세요';
    }
    if (showAir && !Object.values(airData).some(Boolean)) {
      newErrors.airData = '공기질 데이터 항목을 최소 1개 이상 선택하세요';
    }

    if (showAir) {
      airMetaDataItems.forEach(item => {
        if (!item.dataName?.trim()) {
          newErrors[`airMetaDataItems.${item.id}.dataName`] = '데이터 이름을 입력하세요';
        }
      });
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  useEffect(() => {
    const newErrors = { ...errors };

    if (projectType === 'HEALTH_DATA') {
      setAirMetaDataItems([]);
      setAirData(prev => Object.fromEntries(Object.keys(prev).map(k => [k, false])));
      setAirDataConsent('');
      delete newErrors.airDataConsent;
      Object.keys(newErrors).forEach(key => {
        if (key.startsWith('airMetaDataItems.')) delete newErrors[key];
      });
    } else if (projectType === 'AIR_QUALITY') {
      setHealthData(prev => Object.fromEntries(Object.keys(prev).map(k => [k, false])));
      setHealthDataConsent('');
      delete newErrors.healthDataConsent;
    }

    setErrors(newErrors);
  }, [projectType]);

  return {
    projectType, setProjectType,
    title, setTitle,
    participant, setParticipant,
    startDate, setStartDate,
    endDate, setEndDate,
    description, setDescription,
    termsOfPolicy, setTermsOfPolicy,
    privacyPolicy, setPrivacyPolicy,
    healthDataConsent, setHealthDataConsent,
    airDataConsent, setAirDataConsent,
    localDataTermsOfService, setLocalDataTermsOfService,
    personalInfo, setPersonalInfo,
    healthData, setHealthData,
    airData, setAirData,
    airMetaDataItems, setAirMetaDataItems,
    updateMetaDataItem,
    addMetaData,
    removeMetaData,
    showHealth,
    showAir,
    errors,
    validateForm
  };
}
