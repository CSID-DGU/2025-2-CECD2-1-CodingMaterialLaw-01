import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../../api/axiosInstance';
import LogoutButton from '../../components/LogoutButton';

function ProjectListPage() {
  const [projects, setProjects] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProjects = async () => {
      try {
        const res = await axiosInstance.get('/pm/projects');
        setProjects(res.data.data.projectList);
      } catch (error) {
        console.error('프로젝트 목록 불러오기 실패:', error);
      }
    };
    fetchProjects();
  }, []);


  const getTypeLabel = (type) => {
    switch (type) {
      case 'HEALTH_DATA':
        return <span className="text-blue-500">Health Data</span>;
      case 'AIR_QUALITY':
        return <span className="text-red-500">Air Data</span>;
      case 'BOTH':
        return (
          <span>
            <span className="text-blue-500">Health Data</span>
            {' & '}
            <span className="text-red-500">Air Data</span>
          </span>
        );
      default:
        return null;
    }
  };

  return (
    <div className="relative min-h-screen bg-neutral-200 px-4 py-6">
      {/* 오른쪽 상단 고정 로그아웃 버튼 */}
      <LogoutButton />

      <div className="bg-white rounded-2xl shadow-md p-8 w-full max-w-5xl mx-auto">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-bold">My Project</h1>
          <button
            onClick={() => navigate('/projects/create')}
            className="bg-green-300 hover:bg-green-400 text-white font-semibold px-4 py-2 rounded"
          >
            + Create Project
          </button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {projects.map((project) => (
            <div
              key={project.projectId}
              className="border border-gray-200 rounded-xl p-4 bg-gray-100 hover:shadow-md"
            >
              <h2 className="text-xl font-semibold">{project.projectTitle}</h2>
              <p className="text-sm text-gray-600">
                {new Date(project.startDate).toLocaleDateString()} -{' '}
                {new Date(project.endDate).toLocaleDateString()}
              </p>
              <p className="text-sm text-gray-600">Participants: {project.participant || '0'}</p>
              <p className="text-sm mt-1">{getTypeLabel(project.projectType)}</p>
              <button
                onClick={() => navigate(`/projects/${project.projectId}`)}
                className="text-blue-500 text-sm mt-2 underline"
              >
                View Details
              </button>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default ProjectListPage;

// TODO: 현재날짜 이전으로 플젝 시작 불가
// TODO: 달력 UI 개선
// TODO: 위경도 픽스
// TODO: 프로젝트 이름 카멜케이스에서 스페이스로 변경
// TODO: 프로젝트 인원 몇명인지 보여주게 하기