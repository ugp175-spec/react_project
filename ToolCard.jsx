import { Link } from "react-router";
import { ArrowRight } from "lucide-react";
import "./ToolCard.css";

export default function ToolCard({ tool }) {
  const Icon = tool.icon;
  return (
    <Link to={`/tools/${tool.slug}`} className="tool-card">
      <div className="tool-card-icon"><Icon size={23} /></div>
      <span className="tool-card-category">{tool.category}</span>
      <h3>{tool.name}</h3>
      <p>{tool.description}</p>
      <span className="tool-card-link">Open tool <ArrowRight size={15} /></span>
    </Link>
  );
}
