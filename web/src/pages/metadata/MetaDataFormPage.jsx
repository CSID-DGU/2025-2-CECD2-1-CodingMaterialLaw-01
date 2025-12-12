import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axiosInstance from '../../api/axiosInstance';

const MetaDataFormPage = () => {
  const { projectId } = useParams();
  const [metaDataItems, setMetaDataItems] = useState([]);
  const [formData, setFormData] = useState({});
  const [isSubmitted, setIsSubmitted] = useState(false);

  useEffect(() => {
    const fetchMetaDataItems = async () => {
      try {
        const response = await axiosInstance.get(`/metadata/${projectId}/item`);
        const items = response.data.data.metaDataItemList;
        setMetaDataItems(items);

        const initialData = {};
        items.forEach(item => {
          initialData[item.metaDataItemId] = '';
        });
        setFormData(initialData);
      } catch (error) {
        console.error('메타데이터 항목 조회 실패:', error);
      }
    };

    fetchMetaDataItems();
  }, [projectId]);

  const validateInput = (type, value) => {
    switch (type) {
      case 'DOUBLE':
        return /^-?\d+(\.\d+)?$/.test(value);
      case 'INTEGER':
        return /^-?\d+$/.test(value);
      case 'BOOLEAN':
        return /^(true|false)$/i.test(value);
      case 'STRING':
        return typeof value === 'string';
      default:
        return false;
    }
  };

  const handleChange = (e, itemId) => {
    const value = e.target.value;
    setFormData(prev => ({ ...prev, [itemId]: value }));
  };

  const handleSubmit = async () => {
    for (let item of metaDataItems) {
      const value = formData[item.metaDataItemId];
      if (!validateInput(item.dataType, value)) {
        alert(`입력값이 ${item.dataName}의 데이터 타입(${item.dataType})과 일치하지 않습니다.`);
        return;
      }
    }

    const payload = {
      metaDataList: Object.entries(formData).map(([id, value]) => ({
        metaDataItemId: Number(id),
        value: value,
      })),
    };

    try {
      await axiosInstance.post(`/metadata/${projectId}`, payload);
      setIsSubmitted(true);
      alert('메타데이터가 성공적으로 저장되었습니다.');
    } catch (error) {
      console.error('메타데이터 저장 실패:', error);
      alert('저장에 실패했습니다.');
    }
  };

  const getPlaceholder = (type) => {
    switch (type) {
      case 'DOUBLE':
        return '예: 12.34';
      case 'INTEGER':
        return '예: 10';
      case 'BOOLEAN':
        return 'true 또는 false';
      case 'STRING':
        return '텍스트 입력';
      default:
        return '';
    }
  };

  return (
    <div className="max-w-sm mx-auto p-4 bg-white rounded-2xl shadow">
      <h2 className="text-lg font-bold mb-4 border-b pb-2">Meta Data</h2>

      {metaDataItems.map(item => (
        <div className="mb-4" key={item.metaDataItemId}>
          <label className="block font-medium mb-1">{item.dataName}</label>
          <input
            type="text"
            placeholder={getPlaceholder(item.dataType)}
            value={formData[item.metaDataItemId] || ''}
            onChange={(e) => handleChange(e, item.metaDataItemId)}
            className="w-full px-3 py-2 border rounded bg-gray-100"
          />
        </div>
      ))}

      <button
        onClick={handleSubmit}
        disabled={isSubmitted}
        className="w-full py-2 bg-indigo-500 text-white rounded hover:bg-indigo-600 transition disabled:opacity-50"
      >
        {isSubmitted ? 'Submitted' : 'Save'}
      </button>
    </div>
  );
};

export default MetaDataFormPage;
