import { Loader2 } from "lucide-react";

const AppLoader = () => {
    return (
        <div className="flex items-center justify-center bg-background w-full h-full">
            <Loader2 className="h-10 w-10 animate-spin" />
        </div>
    );
};

export default AppLoader;