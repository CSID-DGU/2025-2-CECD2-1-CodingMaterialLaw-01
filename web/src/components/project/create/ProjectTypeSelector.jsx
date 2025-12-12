function ProjectTypeSelector({ projectType, setProjectType, readOnly = false }) {
  const types = ['HEALTH_DATA', 'AIR_QUALITY', 'BOTH'];
  const labels = {
    HEALTH_DATA: 'Health Data',
    AIR_QUALITY: 'Air Data',
    BOTH: 'Health Data & Air Data'
  };

  return (
    <div className="flex gap-4">
      {types.map(type => (
        <label
          key={type}
          className={`flex-1 text-center px-4 py-3 rounded-lg border-2 transition-all duration-150 select-none
            ${projectType === type
              ? 'bg-indigo-600 text-white border-indigo-600 shadow-md'
              : 'bg-white text-indigo-700 border-gray-300'}
            ${readOnly ? 'cursor-default pointer-events-none' : 'cursor-pointer hover:border-indigo-400 hover:bg-indigo-50'}
          `}
        >
          <input
            type="radio"
            name="projectType"
            value={type}
            checked={projectType === type}
            onChange={() => setProjectType(type)}
            className="hidden"
            disabled={readOnly}
          />
          <span className="font-semibold text-base">{labels[type]}</span>
        </label>
      ))}
    </div>
  );
}

export default ProjectTypeSelector;
