import { useState } from "react";
import { Link, NavLink } from "react-router";
import { Menu, X, Shuffle } from "lucide-react";
import "./Navbar.css";

export default function Navbar() {
  const [open, setOpen] = useState(false);
  const close = () => setOpen(false);

  return (
    <header className="navbar">
      <div className="nav-inner">
        <Link to="/" className="brand" onClick={close}>
          <Shuffle size={21} /> <span>Rand<span>mo</span></span>
        </Link>
        <button className="menu-button" onClick={() => setOpen(!open)} aria-label="Toggle menu">
          {open ? <X /> : <Menu />}
        </button>
        <nav className={open ? "nav-links open" : "nav-links"}>
          <NavLink to="/" end onClick={close}>Home</NavLink>
          <NavLink to="/explore" onClick={close}>Tools</NavLink>
          <NavLink to="/saved" onClick={close}>Saved</NavLink>
          <NavLink to="/history" onClick={close}>History</NavLink>
          <NavLink to="/about" onClick={close}>About</NavLink>
        </nav>
      </div>
    </header>
  );
}
